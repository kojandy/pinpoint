/*
 * Copyright 2016 Naver Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.common.server.bo.stat;

/**
 * @author HyunGil Jeong
 */
public class CpuLoadBo extends AgentStatDataBasePoint {

    public static final double UNCOLLECTED_VALUE = -1;

    private double jvmCpuLoad = UNCOLLECTED_VALUE;
    private double systemCpuLoad = UNCOLLECTED_VALUE;


    @Override
    public AgentStatType getAgentStatType() {
        return AgentStatType.CPU_LOAD;
    }

    public double getJvmCpuLoad() {
        return jvmCpuLoad;
    }

    public void setJvmCpuLoad(double jvmCpuLoad) {
        this.jvmCpuLoad = jvmCpuLoad;
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public void setSystemCpuLoad(double systemCpuLoad) {
        this.systemCpuLoad = systemCpuLoad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CpuLoadBo cpuLoadBo = (CpuLoadBo) o;

        if (startTimestamp != cpuLoadBo.startTimestamp) return false;
        if (timestamp != cpuLoadBo.timestamp) return false;
        if (Double.compare(cpuLoadBo.jvmCpuLoad, jvmCpuLoad) != 0) return false;
        if (Double.compare(cpuLoadBo.systemCpuLoad, systemCpuLoad) != 0) return false;
        return agentId != null ? agentId.equals(cpuLoadBo.agentId) : cpuLoadBo.agentId == null;

    }

    @Override
    public int hashCode() {
        int result;
        result = agentId != null ? agentId.hashCode() : 0;
        result = 31 * result + Long.hashCode(startTimestamp);
        result = 31 * result + Long.hashCode(timestamp);
        result = 31 * result + Double.hashCode(jvmCpuLoad);
        result = 31 * result + Double.hashCode(systemCpuLoad);
        return result;
    }

    @Override
    public String toString() {
        return "CpuLoadBo{" +
                "agentId='" + agentId + '\'' +
                ", startTimestamp=" + startTimestamp +
                ", timestamp=" + timestamp +
                ", jvmCpuLoad=" + jvmCpuLoad +
                ", systemCpuLoad=" + systemCpuLoad +
                '}';
    }
}
