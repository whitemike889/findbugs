/*
 * FindBugs - Find bugs in Java programs
 * Copyright (C) 2004-2006 University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.umd.cs.findbugs.detect;


import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.ba.AnalysisContext;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.*;

public class TestingGround extends BytecodeScanningDetector  {

	private static final boolean active 
		 = SystemProperties.getBoolean("findbugs.tg.active");
	

	BugReporter bugReporter;

	OpcodeStack stack = new OpcodeStack();
	public TestingGround(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}


	boolean checked = true;
	@Override
         public void visit(JavaClass obj) {
        if (!checked) {
            checked = true;
             try {
                 JavaClass javaLangClass = Repository.lookupClass("java.lang.Class");
                 bugReporter.reportBug(new BugInstance(this, 
                         "TESTING", HIGH_PRIORITY).addClass(obj).addString(javaLangClass.toString()));
            } catch (ClassNotFoundException e) {
             AnalysisContext.logError("Error looking up java.lang.Class", e);
            }
        }
	}

	@Override
         public void visit(Method obj) {
	}

	@Override
         public void visit(Code obj) {
		// unless active, don't bother dismantling bytecode
		if (active) {
			// System.out.println("TestingGround: " + getFullyQualifiedMethodName());
                	stack.resetForMethodEntry(this);
			super.visit(obj);
		}
	}


	@Override
         public void sawOpcode(int seen) {
		stack.mergeJumps(this);
				stack.sawOpcode(this,seen);
	}
}
