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

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.google.protobuf.Service;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message.Builder;


/**
 * An Apache MINA protocol decoder for decoding a raw binary byte stream of Protcol Buffer RPC service requests
 * 
 * @author Zhenlei Cai (zcai@gaocan.com)
 * @version $Rev$, $Date$
 */
public class RpcRequestDecoder extends CumulativeProtocolDecoder {
    public static final String DECODER_STATE_KEY = RpcRequestDecoder.class.getCanonicalName() + ".State";
    /** max request size is 1MB */ 
    public static final int MAX_REQUEST_LEN = 0x100000;
    static class DecoderState {
	Service service;
	MethodDescriptor methodDesc;
    }

    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
	DecoderState decoderState = (DecoderState) session.getAttribute(DECODER_STATE_KEY);
	if (decoderState == null) {
	    decoderState = new DecoderState();
	    session.setAttribute(DECODER_STATE_KEY, decoderState);
	}
	if (in.prefixedDataAvailable(4, MAX_REQUEST_LEN)) {
	    int rawMsgLen = in.getInt();
	    CharsetDecoder ascii = Charset.forName("US-ASCII").newDecoder();
	    String rpcCallFullName = in.getString(ascii);  // this is the concatenation of service name and method name 
	    int dot = rpcCallFullName.lastIndexOf('.');
	    String serviceFullName = rpcCallFullName.substring(0, dot);
	    String method = rpcCallFullName.substring(dot + 1);
	    
	    Service service = RpcServer.serviceMap.get(serviceFullName);
	    decoderState.service = service;
	    decoderState.methodDesc = service.getDescriptorForType().findMethodByName(method);
	    // decode request
	    byte[] byte_buffer = new byte[rawMsgLen - 1 - rpcCallFullName.length()];
	    Builder builder = service.getRequestPrototype(decoderState.methodDesc).newBuilderForType();
	    in.get(byte_buffer);
	    builder.mergeFrom(byte_buffer);
	    out.write(builder.build());
	    return true;
	} else {
	    // not enough data available
	    return false;
	}
    }
}