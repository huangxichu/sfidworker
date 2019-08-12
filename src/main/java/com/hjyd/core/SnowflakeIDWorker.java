package com.hjyd.core;


import com.hjyd.exception.ApplicationException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program：sfidworker
 * @description：ID生成工作类
 * @author：黄细初
 * @create：2019-07-03 09:47
 */
public class SnowflakeIDWorker {

    /**
     * 每个正式工作站的备份数量
     */
    private static final long BACKUP_COUNT = 1;
    /**
     * 机器ID
     */
    private long workerId;
    /**
     * 数据中心ID
     */
    private long dataCenterId;
    /**
     * 当前序列号
     */
    private long sequence = 0L;
    /**
     * 1562119167519L
     * 起始时间戳，本项目代码上线的时间戳
     * 切记：该值不能修改
     */
    public static long START_STAMP = 1562119167519L;
    /**
     * 机器标识占用的位数
     */
    private long workerIdBits;
    /**
     * 数据中心占用的位数
     */
    private long dataCenterIdBits;
    /**
     * 最大机器ID
     */
    private long maxWorkerId;
    /**
     * 最大数据中心ID
     */
    private long maxDataCenterId;

    /**
     * 计数序列号
     */
    private long sequenceBits;

    private long workerIdShift;

    private long dataCenterIdShift;

    private long timestampLeftShift;

    private long sequenceMask;
    /**
     * 最近一次生成ID的时间戳
     */
    private long lastTimestamp;

    private Map<Long, Long> workerIdLastTimestamp = new ConcurrentHashMap<>();
    private Map<Long, Long> workerIdLastSequence = new ConcurrentHashMap<>();

    /**
     * 最大容忍时间，单位毫秒，即如果时间只是回拨了该变量指定的时间，那么就等待相应的时间就可；
     * 考虑性能问题，该值不能过大。
     */
    private long max_backward_ms = 3;

    public static class Factory {
        /**
         * 每一个部分占用的位数默认值
         */
        /**
         * 机器标识占用的位数
         */
        private final static int DEFAULT_MACHINE_BIT_NUM = 5;
        /**
         * 数据中心占用的位数
         */
        private final static int DEFAULT_IDC_BIT_NUM = 5;

        private int machineBitNum;

        private int idcBitNum;

        public Factory() {
            this.machineBitNum = DEFAULT_MACHINE_BIT_NUM;
            this.idcBitNum = DEFAULT_IDC_BIT_NUM;
        }


        public Factory(int machineBitNum, int idcBitNum) {
            this.machineBitNum = machineBitNum;
            this.idcBitNum = idcBitNum;
        }

        public SnowflakeIDWorker create(long workerId, long dataCenterId) {
            return new SnowflakeIDWorker(workerId, dataCenterId, machineBitNum, idcBitNum);
        }
    }

    private SnowflakeIDWorker(long workerId, long dataCenterId, int workerIdBits, int dataCenterIdBits) {
        this.workerIdBits = workerIdBits;
        this.dataCenterIdBits = dataCenterIdBits;
//        this.maxWorkerId = ~(-1L << (int) (this.workerIdBits - this.BACKUP_COUNT));//留一部分作为预留工作站，防止时钟回拨
        this.maxWorkerId = ((1L << (int) this.workerIdBits) / (this.BACKUP_COUNT + 1));//留一部分作为预留工作站，防止时钟回拨
        this.maxDataCenterId = ~(-1L << (int) this.dataCenterIdBits);
        this.sequenceBits = 12L;
        this.workerIdShift = this.sequenceBits;
        this.dataCenterIdShift = this.sequenceBits + this.workerIdBits;
        this.timestampLeftShift = this.sequenceBits + this.workerIdBits + this.dataCenterIdBits;
        this.sequenceMask = ~(-1L << (int) this.sequenceBits);
        this.lastTimestamp = -1L;
        if (workerId <= this.maxWorkerId && workerId >= 0L) {
            if (dataCenterId <= this.maxDataCenterId && dataCenterId >= 0L) {
                this.workerId = workerId;
                this.dataCenterId = dataCenterId;
//                this.workerIdLastTimestamp.put(workerId, -1L);
                //备份生产ID的最后时间
                for (int i = 1; i <= BACKUP_COUNT; i++) {
                    this.workerIdLastTimestamp.put(workerId + i * this.maxWorkerId, -1L);
                    this.workerIdLastSequence.put(workerId + i * this.maxWorkerId, 0L);
                }
            } else {
                throw new IllegalArgumentException(String.format("dataCenter Id can't be greater than %d or less than 0", this.maxDataCenterId));
            }
        } else {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
        }
    }

    public synchronized long nextId() {
        //获取当前时间
        long timestamp = this.timeGen();

        if (timestamp < this.lastTimestamp) {
            long diff = this.lastTimestamp - timestamp;
            //时钟回拨在可接受范围
            if (diff < max_backward_ms) {
                try {
                    Thread.sleep(diff);
                    //重置
                    timestamp = this.timeGen();
                } catch (InterruptedException e) {
                    throw new ApplicationException(e, String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", this.lastTimestamp - timestamp));
                }
            } else {
                //使用备份workerID
                return tryGenerateKeyOnBackup(timestamp);
            }

        }
        if (this.lastTimestamp == timestamp) {
            this.sequence = this.sequence + 1L & this.sequenceMask;
            if (this.sequence == 0L) {
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0L;
        }

        this.lastTimestamp = timestamp;
        return timestamp - START_STAMP << (int) this.timestampLeftShift | this.dataCenterId << (int) this.dataCenterIdShift | this.workerId << (int) this.workerIdShift | this.sequence;
    }

    /**
     * @Description：尝试在workerId的备份workerId上生产ID
     * @Author：黄细初
     * @Date：2019/7/5
     */
    private long tryGenerateKeyOnBackup(long currentMillis) {
//        for (Map.Entry<Long, Long> entry : workerIdLastTimestamp.entrySet()) {
//            //取得备用workerId的lastTime
//            Long tempLastTime = entry.getValue();
//
//        }
        long workerId_backup = -1L;
        Long tempLastTime = -1L;
        Long tempSequence = 0L;
        for (int i = 1; i <= BACKUP_COUNT; i++) {
            //取得备用workerId的lastTime
            Long key = workerId + i * this.maxWorkerId;
            tempSequence = this.workerIdLastSequence.get(key);
            tempLastTime = this.workerIdLastTimestamp.get(key);
            tempLastTime = tempLastTime == null ? 0L : tempLastTime;
            if (tempLastTime <= currentMillis) {
                workerId_backup = key;
            }
        }
        if (workerId_backup == -1L) {
            throw new ApplicationException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", this.lastTimestamp - currentMillis));
        }
        if (tempLastTime == currentMillis) {
            tempSequence = tempSequence + 1L & this.sequenceMask;
            if (tempSequence == 0L) {
                currentMillis = this.tilNextMillis(tempLastTime);
            }
        } else {
            tempSequence = 0L;
        }
        this.workerIdLastSequence.put(workerId_backup, tempSequence);
        this.workerIdLastTimestamp.put(workerId_backup, currentMillis);
        return currentMillis - START_STAMP << (int) this.timestampLeftShift | this.dataCenterId << (int) this.dataCenterIdShift | workerId_backup << (int) this.workerIdShift | tempSequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for (timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
        }

        return timestamp;
    }


    protected long timeGen() {
        long currTime = System.currentTimeMillis();
        return currTime;
    }

    public static void main(String[] args) {
//        System.out.println(System.currentTimeMillis());
//        System.out.println(~(-1L << (int) 4));
        System.out.println(2<<1);
        System.out.println(2<<1|9);
        System.out.println(15-2<<1|9);
        //模拟时钟回拨
//        Map<Long, Long> countMap = new HashMap<>();
//        SnowflakeIDWorker.Factory factory = new SnowflakeIDWorker.Factory(6, 4);
//        SnowflakeIDWorker idWorker = factory.create(1, 0);
//        long end_time = System.currentTimeMillis() + 60 * 1000;
//        while (true) {
//            long currTime = System.currentTimeMillis();
//
//            if (currTime > end_time) {
//                break;
//            }
//            Long id = idWorker.nextId();
//            Long idcount = countMap.get(id);
//            if (idcount == null) {
//                idcount = 1L;
//            } else {
//                idcount += 1;
//                System.out.println(String.format("ID值 [%s] 出现了 [%d]次", id.toString(), idcount));
//                break;
//            }
//            countMap.put(id, idcount);
//            System.out.println(String.format("ID值 [%s] 出现了 [%d]次", id.toString(), idcount));
//        }
    }
}