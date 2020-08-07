package schedulingAlgorithm.geneticAlgorithm;

/**
 * 染色体
 * @author lin
 * @Date 2020.3.6
 */
public class Gene {
	// 染色体适应度
	public double fitness;
	// 染色体集合
	public Integer[] chromosome;

	/**
	 * 初始化染色体适应度
	 */
	public Gene() {
		fitness = 0;
	}

	/**
	 * 设置染色体适应度
	 * @param fitness
	 */
	public Gene(double fitness) {
		this.fitness = fitness;
	}
}
