package com.aiworkflow.common.generator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * 雪花ID生成器
 * 
 * @author AI Workflow Team
 */
public class SnowflakeGenerator implements IdentifierGenerator {

    private static final SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(1, 1);

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return String.valueOf(ID_WORKER.nextId());
    }

    /**
     * 雪花ID算法实现
     */
    public static class SnowflakeIdWorker {

        /** 开始时间戳 */
        private static final long START_TIMESTAMP = 1640995200000L; // 2022-01-01

        /** 序列号占用的位数 */
        private static final long SEQUENCE_BIT = 12;

        /** 机器标识占用的位数 */
        private static final long MACHINE_BIT = 5;

        /** 数据中心占用的位数 */
        private static final long DATACENTER_BIT = 5;

        /** 支持的最大数据中心ID */
        private static final long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);

        /** 支持的最大机器ID */
        private static final long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);

        /** 支持的最大序列号 */
        private static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

        /** 机器ID向左的位移 */
        private static final long MACHINE_LEFT = SEQUENCE_BIT;

        /** 数据中心ID向左的位移 */
        private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

        /** 时间戳向左的位移 */
        private static final long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

        private long datacenterId;
        private long machineId;
        private long sequence = 0L;
        private long lastTimestamp = -1L;

        public SnowflakeIdWorker(long datacenterId, long machineId) {
            if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
                throw new IllegalArgumentException("datacenter ID can't be greater than MAX_DATACENTER_NUM or less than 0");
            }
            if (machineId > MAX_MACHINE_NUM || machineId < 0) {
                throw new IllegalArgumentException("machine ID can't be greater than MAX_MACHINE_NUM or less than 0");
            }
            this.datacenterId = datacenterId;
            this.machineId = machineId;
        }

        /**
         * 产生下一个ID
         */
        public synchronized long nextId() {
            long currentTimestamp = getNewTimestamp();
            if (currentTimestamp < lastTimestamp) {
                throw new RuntimeException("Clock moved backwards. Refusing to generate id");
            }

            if (currentTimestamp == lastTimestamp) {
                sequence = (sequence + 1) & MAX_SEQUENCE;
                if (sequence == 0L) {
                    currentTimestamp = getNextMill();
                }
            } else {
                sequence = 0L;
            }

            lastTimestamp = currentTimestamp;

            return (currentTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT
                    | datacenterId << DATACENTER_LEFT
                    | machineId << MACHINE_LEFT
                    | sequence;
        }

        private long getNextMill() {
            long mill = getNewTimestamp();
            while (mill <= lastTimestamp) {
                mill = getNewTimestamp();
            }
            return mill;
        }

        private long getNewTimestamp() {
            return System.currentTimeMillis();
        }
    }
}