/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  Neither the name of the Mario AI nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package competition.gic2010.turing.Gaudl;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;

import competition.gic2010.turing.Gaudl.gp.MarioData;
import org.platformer.agents.Agent;
import org.platformer.benchmark.platform.environments.Environment;

/**
 * Created by IntelliJ IDEA.
 * User: Swen Gaudl, swen.gaudl@gmail.com
 * Date: 11/27/12
 * Time: 1:47 AM
 * Package: touring.Gaudl
 */

public class Mario_GPAgent implements Agent
{
	MarioData m_data;
	String agentName;
	IGPProgram executionCode;
	Object[] executionArguments;
	
	public Mario_GPAgent(IGPProgram prog, Object[] args,MarioData data) {
		m_data = data;
		executionArguments = args;
		executionCode = prog;
	}
	
	@Override
	public boolean[] getAction() {
		boolean [] output = new boolean[Environment.numberOfKeys];
		executionCode.execute_void(0, executionArguments);
		//executionCode.execute_boolean(0, executionArguments);
		if (m_data instanceof MarioData)
			output = m_data.getActions();
		
		return output;
	}
	
	

	@Override
	public void integrateObservation(Environment environment) {
		byte[][] levelScene = environment.getLevelSceneObservationZ(1);
	    byte[][] enemies = environment.getEnemiesObservationZ(0);
	    byte[][] mergedObservation = environment.getMergedObservationZZ(1, 0);
		
		m_data.setEnvironment(environment);
	}

	@Override
	public void giveIntermediateReward(float intermediateReward) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
	}

	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow,
			int egoCol) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return this.agentName;
	}

	@Override
	public void setName(String name) {
		this.agentName = name;
		
	}
    
}
