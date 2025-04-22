############################
#####     LOCALHOST    #####
############################

kafka-topics --bootstrap-server localhost:9092 --topic first_topic --create --partitions 1

# producing
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic
> Hello World
>My name is Ruelo
>I love Kafka
>^C  (<- Ctrl + C is used to exit the producer)


# producing with properties
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic --producer-property acks=all
> some message that is acked
> just for fun
> fun learning!

# produce with keys
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic --property parse.key=true --property key.separator=:
>example key:example value
>name:Stephane