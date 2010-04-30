/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package com.googlecode.protobuf.minarpc;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.googlecode.protobuf.minarpc.RpcRequestDecoder.DecoderState;



/**
 * Handle decoded RPC request by invoking the correct protobuf service in the registry and send back the response.
 *  
 * @author Zhenlei Cai (zcai@gaocan.com)
 * @version $Rev$, $Date$
 */

public class ProtobufRpcServerHandler extends IoHandlerAdapter {

    public void messageReceived(final IoSession session, Object message) throws Exception {
	Message request = (Message) message;
	DecoderState decoderState = (DecoderState) session.getAttribute(RpcRequestDecoder.DECODER_STATE_KEY);
	RpcCallback<Message> cb = new RpcCallback<Message>() {
	    @Override
	    public void run(Message resp) {
		session.write(resp);
	    }
	};
	decoderState.service.callMethod(decoderState.methodDesc, new NullRpcController(), request, cb);
    }

    public void messageSent(IoSession session, Object message) throws Exception {
	// Empty handler
	System.out.println("sent " + message.toString());
    }

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
	cause.printStackTrace();
    }

    public void sessionCreated(IoSession session) throws Exception {
	System.out.println("client session created");
    }

    public void sessionOpened(IoSession session) throws Exception {
	// Empty handler
    }

    public void sessionClosed(IoSession session) throws Exception {
	System.out.println("client closed");
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
	System.out.println("client idled");
    }
}
