#!/bin/bash

# Load environment variables from .env file
set -a
source .env
set +a

# Variables
S3_BUCKET="lukeria-db/backup"

# Ensure the current directory is writable
cd /home/ubuntu/lukeria_v2 || { echo "Failed to change directory"; exit 1; }

# Download the latest backup from S3
LATEST_BACKUP=$(aws s3 ls s3://$S3_BUCKET/ | awk '{print $4}' | grep -E '^mysql_backup_' | sort | tail -n 1)
if [ -z "$LATEST_BACKUP" ]; then
  echo "No backup found in S3 bucket."
  exit 1
fi

aws s3 cp "s3://$S3_BUCKET/$LATEST_BACKUP" . || { echo "Failed to download backup from S3"; exit 1; }

# Stop the MySQL container
docker stop mysql || { echo "Failed to stop MySQL container"; exit 1; }

# Remove existing data in the MySQL volume
docker run --rm -v mysql-data:/volume busybox rm -rf /volume/* || { echo "Failed to remove existing data"; exit 1; }

# Start the MySQL container
docker start mysql || { echo "Failed to start MySQL container"; exit 1; }

# Wait for the MySQL container to be ready
sleep 10

# Restore the MySQL database
gunzip < "$LATEST_BACKUP" | docker exec -i mysql mysql -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE || { echo "Failed to restore backup"; exit 1; }

# Clean up local backup file
rm -f "$LATEST_BACKUP" || { echo "Failed to delete local backup file"; exit 1; }

echo "Restore completed successfully."
