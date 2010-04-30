"""
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
 
  Author: Zhenlei Cai (zcai@gaocan.com)
"""
 

from arithmetic_pb2 import ArithmeticService_Stub
import arithmetic_pb2
from protobuf.rpc_channel import MinaRpcChannel,MinaRpcController
import optparse



if __name__ == '__main__':
    channel = MinaRpcChannel('localhost', 33789)

    p = optparse.OptionParser()
    p.add_option('-n', '--num_ints', default='8')
    options,arguments = p.parse_args()

    service = ArithmeticService_Stub(channel)
    req = arithmetic_pb2.OpRequest()
    req.operator = '+'
    upto = int(options.num_ints)
    for n in xrange(1,  upto + 1):
        req.operands.append(n)
    print 'add up numbers from 1 to ', upto
    print service.performOperation(MinaRpcController(),req)


