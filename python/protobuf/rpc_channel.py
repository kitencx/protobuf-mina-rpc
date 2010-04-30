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

from google.protobuf.service import RpcChannel
from google.protobuf.service import RpcController
import socket, struct


class MinaRpcChannel(RpcChannel):
    def __init__(self, server_host, port):
        self.sock = socket.socket()
        self.sock.connect((server_host, port))
        
    def CallMethod(self, method_descriptor, rpc_controller,
                 request, response_class, done):
        """
        client calling service method is always blocked
        until the call returns.
        
        Args:
          method_descriptor: Descriptor of the invoked method.
          rpc_controller: Rpc controller to execute the method.
          request: Request protocol message.
          done: callback , ignored at this time due to synchronous nature of the call
        Returns:
          Response message (in case of blocking call).
        """
        #

        req = request.SerializeToString()

        
        buf = method_descriptor.full_name + '\0' + req
        # print len(buf), " bytes"
        size_header = struct.pack('>i', len(buf))
        self.sock.sendall(size_header + buf)
        
        # block and wait to receive the response
        buf = self.sock.recv(4)
        # 4 bytes are the size header (big endian)
        while len(buf) != 4:
            buf.extend(self.sock.recv(1))

        size = struct.unpack('>i', buf) [0]
        buf = self.sock.recv(size)
        while len(buf) < size:
            buf.extend(self.sock.recv(size - len(buf)))
        resp = response_class()
        resp.MergeFromString(buf)
        return resp

    
    def __del__(self):
        self.sock.close()

class MinaRpcController(RpcController):
    pass
