1. Move the docker-compose file to the root directory (consists of lukeria-erp-api and lukeria-erp-frond-end)
2. Move the Dockerfile to the root directory of each service
3. Create a backend.env and frontend.env file in the root directory next to the docker-compose file
4. backup_mysql.sh and restore_mysql.sh files to the root directory #edit them accordingly
5. Create a cron job to run the backup_mysql.sh script every day or another time you want
   -> create a cron job for current user [ubuntu] -> 'crontab -e'
   -> add the following line to the file: '0 2 * * * /bin/bash /home/ubuntu/root_directory/backup_mysql.sh >> /home/ubuntu/backup.log 2>&1
   -> chmod +x /home/ubuntu/root_directory/backup_mysql.sh #Give Execution Rights
   -> check if your cron job is saved by running 'crontab -l'
   -> .env file needs to be in the same folder
   *-> run the scripts manually './backup_mysql.sh or ./restore_mysql.sh'
