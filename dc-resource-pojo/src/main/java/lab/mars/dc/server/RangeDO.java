package lab.mars.dc.server;

/**
 * Author:yaoalong.
 * Date:2016/4/22.
 * Email:yaoalong@foxmail.com
 */
public class RangeDO {
    private long start;
    private long end;
    public RangeDO(long start,long end){
        this.start=start;
        this.end=end;
    }
    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
