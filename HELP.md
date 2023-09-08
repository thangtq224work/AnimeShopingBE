# Read Me First
FE : https://github.com/thangtq224work/AnimeShopingFE
### Run App with docker in window os
* required : docker , jdk 17 : 
* how to install ? =>  jdk : https://www.youtube.com/watch?v=ykAhL1IoQUM ; 
<!-- step 1 : pull mysql image -->
docker pull mysql:8.0
<!-- docker rm -f mysqldbforbuild_1939399393 -->
<!-- step 2 : run test mysql server for build -->
docker run -dp 4000:3306 --name mysqldbforbuild_1939399393 -e MYSQL_ROOT_PASSWORD=thang1212 -e MYSQL_DATABASE=animeshop mysql:8.0
<!-- step 3 : build file jar -->
./mvnw package
<!-- step 4 : build image from Dockerfile -->
docker build -t animebe .
<!-- step 5 : clean test database -->
docker rm -f mysqldbforbuild_1939399393
<!-- step 6 : create docker network -->
docker network create mysqlnet
<!-- step 7 : run mysql server  -->
<!-- run in background  -->
docker run -d --name mysqldb -v mysql_data:/var/lib/mysql --network mysqlnet -e MYSQL_ROOT_PASSWORD=thang1212 -e MYSQL_DATABASE=animeshop -p 3307:3306 mysql:8.0
<!-- if in current terminal -->
docker run --name mysqldb -v mysql_data:/var/lib/mysql --network mysqlnet -e MYSQL_ROOT_PASSWORD=thang1212 -e MYSQL_DATABASE=animeshop -p 3307:3306 mysql:8.0
<!-- step 8 : copy file sql to server -->
docker cp .\procedure.sql mysqldb:/tmp/procedure.sql
<!-- step 9 : run file sql -->
docker exec -it mysqldb bash -c 'mysql -u root -p animeshop; </tmp/procedure.sql'
<!-- docker exec -it mysqldb bash -c 'mysql -u root -p -D animeshop; </tmp/procedure.sql' -->
<!-- step 10 : run backend app  -->
docker run -e MYSQL_HOST=mysqldb -e MYSQL_USERNAME=root  -e MYSQL_PASSWORD=thang1212 -e MYSQL_DATABASE=animeshop -e MYSQL_PORT=3306 --network mysqlnet -p 8080:8080 animebe 

<!-- stop  -->