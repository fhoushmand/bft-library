/*
 *  Copyright 2012 Carnegie Mellon University  
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */
package bftsmart.hermes.deployment.network;

import bftsmart.hermes.serialization.HermesSerializable;
import bftsmart.hermes.serialization.HermesSerializableHelper;
import java.nio.ByteBuffer;

/**
 *
 * @author Rolando Martins <rolandomartins@cmu.edu>
 */
public class EnvElement implements HermesSerializable {
    String m_var;
    String m_value;
    
    public EnvElement(){
        
    }
    
    public EnvElement(String var,String value){
        m_var = var;
        m_value = value;
    }
    
    public String getVar(){
        return m_var;
    }
    
    public String getValue(){
        return m_value;
    }

    @Override
    public void serializable(ByteBuffer buf) throws Exception {
        HermesSerializableHelper.serializeString(buf, m_var);
        HermesSerializableHelper.serializeString(buf, m_value);
    }

    @Override
    public void deserializable(ByteBuffer buf) throws Exception {
        m_var = HermesSerializableHelper.deserializeString(buf);
        m_value = HermesSerializableHelper.deserializeString(buf);
    }
}
