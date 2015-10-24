用作把antx.properties写入资源文件的PlaceHolder，类似Spring的PropertyPlaceholderConfigurer。 

1. pom文件添加plugin

<plugin>  
	<groupId>com.shaobo</groupId>  
	<artifactId>go2config</artifactId>  
	<version>1.0-SNAPSHOT</version>  
	<configuration>  
		<includes>  
		   <include>D:/sc/src/main/resources/dubbo/**.xml</include>  
		</includes>  
	</configuration>  
</plugin>  

2. mvn go2config:gogo 