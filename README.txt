PURPOSE:


An RPC channel implementation for Google Protocol Buffer. Currently only synchronously calling Java side protobuf services from a Python client is supported.  Sample Python client code (see python/examples for details):

      
    channel = MinaRpcChannel('localhost', 33789)
    service = ArithmeticService_Stub(channel)
    req = arithmetic_pb2.OpRequest()
    ...
    # calling the service which runs on Java side , this is a blocking call
    result = service.performOperation(MinaRpcController(),req)


At Java side, Apache MINA is used to run a non-blocking socket server to serve protobuf RPC requests.


BUILD and INSTALL:

After checking out the source
Java :  
    cd java 
    ant  (this will build dist/protobuf-mina-rpc.jar)
    ant demo (this will start a simple arithmetic service server)    

Python:
    cd python
    python arithmetic_client.py  -n <num>
    (<num> is an integer,  this will first build a list [1,2,3,...,num] at the Python side and then   invoke the Java sides arithmetic service to compute the sum of this list, and finally print out the result that comes back. As an example: 

python arithmetic_client.py  -n 5
add up numbers from 1 to  5
result: 15

By changing the value of <num> , one can control the size of the RPC request sent to the Java side, this is a nice way to test the performance of the RPC given big requests.


TODO:
Java RPC client and Python RPC server, async support.


License:
Apache 2.0, also see the sources


Author:

Zhenlei Cai (zcai@gaocan.com)

Version:
0.1 , April 29,2010
