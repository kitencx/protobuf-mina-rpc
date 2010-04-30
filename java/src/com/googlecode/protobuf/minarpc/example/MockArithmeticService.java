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

package com.googlecode.protobuf.minarpc.example;

import java.util.List;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.minarpc.example.Arithmetic.ArithmeticService;
import com.googlecode.protobuf.minarpc.example.Arithmetic.OpRequest;
import com.googlecode.protobuf.minarpc.example.Arithmetic.OpResponse;

/**
 * An simple Protcol Buffer service class that computes the sum (or product, depending on the operator parameter)
 * of a list integers
 * 
 * @author Zhenlei Cai (zcai@gaocan.com)
 * @version $Rev$, $Date$
 */

public class MockArithmeticService extends ArithmeticService {

    @Override
    public void performOperation(RpcController controller, OpRequest request, RpcCallback<OpResponse> done) {
	String op = request.getOperator();
	List<Integer> operands = request.getOperandsList();
	int result = 0;
	if (op.equals("+")) {
	    for (int operand : operands) {
		result += operand;
	    }
	} else if (op.equals("*")) {
	    result = 1;
	    for (int operand : operands) {
		result *= operand;
	    }
	} else {
	    throw new IllegalArgumentException("Operator " + op + " is not allowed");
	}
	OpResponse.Builder builder = OpResponse.newBuilder();
	builder.setResult(result);
	done.run(builder.build());
    }

}