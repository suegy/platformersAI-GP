/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
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

package ch.idsia.scenarios;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.ForwardAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

import java.io.IOException;

import org.jgap.gp.IGPProgram;

import competition.gic2010.turing.Gaudl.Mario_GPAgent;
import competition.gic2010.turing.Gaudl.gp.MarioData;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey@idsia.ch
 * Date: May 7, 2009
 * Time: 4:38:23 PM
 * Package: ch.idsia
 */

public class DataAcquisistion
{
	
	MarioAIOptions m_options;
	BasicTask m_task;

	public DataAcquisistion(MarioAIOptions opts){
		
		this.m_options = opts;
		m_task = new BasicTask(opts);
		String name = m_options.getRecordingFileName();
		//for (int i = 0; i < 5; ++i)
		//{
		int seed = 0;
		int difficulty = m_options.getLevelDifficulty();
		int trial = 0;
		do
		{
			
			runMarioTask(200, seed, difficulty, name,trial++);
			System.out.println(m_task.getEnvironment().getEvaluationInfoAsString());
			//      System.out.println("Seed: "+seed+" Try: "+i);
		} while (m_task.getEnvironment().getEvaluationInfo().marioStatus != Environment.MARIO_STATUS_WIN && trial <= 5);
		trial = 0;
		seed++;
		do
		{
			
			runMarioTask(200, seed, difficulty, name,trial++);
			System.out.println(m_task.getEnvironment().getEvaluationInfoAsString());
			//      System.out.println("Seed: "+seed+" Try: "+i);
		} while (m_task.getEnvironment().getEvaluationInfo().marioStatus != Environment.MARIO_STATUS_WIN && trial <= 5);
		//}
		Runtime rt = Runtime.getRuntime();
	}
	
	protected boolean  runMarioTask(int time,int lvl,int difficulty,String name,int trial) {
		m_options.setRecordFile(String.format("players-%s-lvl-%s-time-%s-difficulty-%s-trial-%s",name,lvl,time,difficulty,trial));
		m_options.setTimeLimit(time);
		m_options.setLevelRandSeed(lvl);
		m_options.setLevelDifficulty(difficulty);
		//m_options.setLevelRandSeed(151079);
		m_task.setOptionsAndReset(m_options);

		return m_task.runSingleEpisode(1);
	}


	public static void main(String[] args)
	{
		//final String argsString = "-vis on";
		DataAcquisistion test = new DataAcquisistion(new MarioAIOptions(args));

		

		System.exit(0);

	}
}
