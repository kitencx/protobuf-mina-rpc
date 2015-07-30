An RPC channel implementation for Google Protocol Buffer. Currently only synchronously calling Java protobuf services from a Python client is supported.

At Java side, Apache MINA (http://mina.apache.org/features.html) is used to run the server which means  things like SSL, JMX, custom threading could be done easily and transports other than TCP are possible.

Sample server code that has one protobuf service registered:

```
    public static void main(String[] args) throws IOException {
	RpcServer server = new RpcServer("localhost", 33789);
	MockArithmeticService service = new MockArithmeticService();
	RpcServer.registerServiceImpl(service);
	server.start();
    }
```

Sample Python client code (see python/examples for details):

```
      
    channel = MinaRpcChannel('localhost', 33789)
    service = ArithmeticService_Stub(channel)
    req = arithmetic_pb2.OpRequest()
    ...
    # calling the service which runs on Java side , this is a blocking call
    result = service.performOperation(MinaRpcController(),req)
```