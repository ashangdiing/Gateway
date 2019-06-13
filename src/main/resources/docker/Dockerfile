FROM java
MAINTAINER augustwei
# VOLUME /data
#WORKDIR /data
ADD gateway-0.0.1-SNAPSHOT.jar data/gateway-0.0.1-SNAPSHOT.jar
RUN sh -c 'touch data/gateway-0.0.1-SNAPSHOT.jar'
ENV JAVA_OPTS=""
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","data/gateway-0.0.1-SNAPSHOT.jar"]
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# FROM ：表示使用 Jdk8 环境 为基础镜像，如果镜像不是本地的会从 DockerHub 进行下载
# MAINTAINER ：指定维护者的信息
# VOLUME ：VOLUME 指向了一个/data的目录，由于 Spring Boot 使用内置的Tomcat容器，Tomcat 默认使用/data作为工作目录。这个命令的效果是：在宿主机的/var/lib/docker目录下创建一个临时文件并把它链接到容器中的/data目录
# ADD ：拷贝文件并且重命名(前面是上传jar包的名字，后面是重命名)
# RUN ：每条run指令在当前基础镜像执行，并且提交新镜像
# ENTRYPOINT ：为了缩短 Tomcat 的启动时间，添加java.security.egd的系统属性指向/dev/urandom作为 ENTRYPOINT
# WORKDIR 设置工作目录，相当于CD命令
# 执行命令
# docker build -t gateway .

 

# --hostname ：指定hostname;
# --net : 指定网络模式
# --ip：指定IP
# --add-host ：指定往/etc/hosts添加的host。格式 --add-host hadoop2:192.168.10.32
#使用外部的docker镜像和配置文件。
# docker run --network=host --name gatewayInner -it -v /docker/java/gateway/logs:/logs -v /docker/java/gateway/application.properties:/application.properties -p 9005:9005 -p 9006:9006 --restart=always -d gateway 
