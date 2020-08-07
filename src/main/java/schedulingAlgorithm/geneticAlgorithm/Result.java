package schedulingAlgorithm.geneticAlgorithm;

/**
 * 计算结果集
 * @author lin
 * @Date 2020.3.6
 */
public class Result {
	// 总耗时时间
	public int fulfillTime = 0;
	// 设备工作时间列表
	public int[] machineWorkTime = new int[1024];
	// 工件工序顺序
	public int[] processIds = new int[1024];
	// 工件工序开始时间
	public int[][] startTime = new int[1024][1024];
	// 工件工序结束时间
	public int[][] endTime = new int[1024][1024];
}
