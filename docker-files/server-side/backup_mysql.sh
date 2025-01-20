#!/bin/bash

# Load environment variables from .env file
set -a
source /home/ubuntu/lukeria_v2/.env
set +a

# Variables
S3_BUCKET="lukeria-db/backup"
DATE=$(date +%Y-%m-%d_%H-%M-%S)
BACKUP_FILE="mysql_backup_$DATE.sql.gz"
PREVIOUS_BACKUP_PREFIX="mysql_backup_"

# Ensure the current directory is writable
cd /home/ubuntu/lukeria_v2 || { echo "Failed to change directory"; exit 1; }

# Backup the MySQL database
docker exec -i mysql mysqldump -u $MYSQL_USER -p$MYSQL_PASSWORD --databases $MYSQL_DATABASE --no-tablespaces | gzip > $BACKUP_FILE || { echo "Failed to create backup"; exit 1; }

# Upload the backup to S3
aws s3 cp $BACKUP_FILE s3://$S3_BUCKET/$BACKUP_FILE || { echo "Failed to upload backup to S3"; exit 1; }

# List all backup files in S3 and keep only the two most recent ones
BACKUP_FILES=$(aws s3 ls s3://$S3_BUCKET/ | awk '{print $4}' | grep -E "^$PREVIOUS_BACKUP_PREFIX" | sort)
BACKUP_COUNT=$(echo "$BACKUP_FILES" | wc -l)

if [ "$BACKUP_COUNT" -gt 2 ]; then
  # Delete the oldest backup(s) if there are more than 2
  OLD_BACKUPS=$(echo "$BACKUP_FILES" | head -n $(($BACKUP_COUNT - 2)))
  for OLD_BACKUP in $OLD_BACKUPS; do
    aws s3 rm "s3://$S3_BUCKET/$OLD_BACKUP" || { echo "Failed to delete old backup from S3: $OLD_BACKUP"; exit 1; }
    echo "Deleted old backup: $OLD_BACKUP"
  done
fi

# Clean up local backup file
rm -f $BACKUP_FILE || { echo "Failed to delete local backup file"; exit 1; }
