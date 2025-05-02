# wikimedia-recent-change-proto

The protobuf contracts shared between components for wikimedia stream

## wikimedia-recent-change-proto contract

**Usage**  
The _wikimedia.proto_ will contain the message payload containing the _wikimedia_ details.

**Including the contracts in your project:**  
For a java project consider using the following in your pom.xml

```xml
    <dependencies>
        <dependency>
            <groupId>com.rueloparente</groupId>
            <artifactId>wikimedia-recent-change-proto</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>${maven-dependency-plugin.version}</version>
                <executions>
                    <execution>
                        <id>unpack-dependencies</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>unpack-dependencies</goal>
                        </goals>
                        <configuration>
                            <includes>**/*.proto</includes>
                            <excludes>**google/protobuf/**/*.proto</excludes>
                            <outputDirectory>${basedir}/target/proto</outputDirectory>
                            <overWriteReleases>false</overWriteReleases>
                            <overWriteSnapshots>true</overWriteSnapshots>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>${protobuf-maven-plugin.version}</version>
                <extensions>true</extensions>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>test-compile</goal>
                        </goals>
                        <configuration>
                            <protoSourceRoot>${basedir}/target/proto</protoSourceRoot>
                            <protocArtifact>
                                com.google.protobuf:protoc:${protobuf-java.version}:exe:${os.detected.classifier}
                            </protocArtifact>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```