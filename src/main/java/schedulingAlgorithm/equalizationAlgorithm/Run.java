package schedulingAlgorithm.equalizationAlgorithm;

import java.util.*;

/**
 * 亚明生产车间工件分配均衡算法实例
 * @author lin
 * @Date 2020.3.9
 */
public class Run {
	/**
	 * 应用场景：一个工件可以有多个设备加工，但只能在一个设备上进行加工
	 * 当多个工件对应多个设备时，将每个工件分配到每个设备上，使设备达到最大负载
	 * @param args
	 */
	static public void main(String[] args) {
		System.out.println("亚明生产车间工件分配均衡算法开始执行！");
		// 程序运行开始时间
		Date startTime = new Date();
		// 设置生产基本信息
		// 每个list的内容为工序，[0]为设备编号， [1]为加工耗时
		List<List<Integer[][]>> job = Arrays.asList(
				Arrays.asList(new Integer[][]{{2, 3}, {8}}, new Integer[][]{{0}, {8}}, new Integer[][]{{2, 3}, {4}}, new Integer[][]{{2, 3}, {24}}, new Integer[][]{{2, 3}, {6}}),
				Arrays.asList(new Integer[][]{{0}, {4}}, new Integer[][]{{2, 3}, {5}}, new Integer[][]{{2, 3}, {3}}, new Integer[][]{{2, 3}, {4}}),
				Arrays.asList(new Integer[][]{{2, 3}, {3}}, new Integer[][]{{2, 3}, {7}}, new Integer[][]{{0}, {15}}, new Integer[][]{{1}, {20}}, new Integer[][]{{0}, {8}}),
				Arrays.asList(new Integer[][]{{1, 2}, {7}}, new Integer[][]{{2, 3}, {6}}, new Integer[][]{{2, 3}, {21}}, new Integer[][]{{0}, {1}}, new Integer[][]{{2, 3}, {16}}, new Integer[][]{{2, 3}, {3}}),
				Arrays.asList(new Integer[][]{{2, 3}, {10}}, new Integer[][]{{1}, {4}}, new Integer[][]{{2, 3}, {8}}, new Integer[][]{{2, 3}, {4}}, new Integer[][]{{0}, {12}}, new Integer[][]{{2, 3}, {6}}, new Integer[][]{{2, 3}, {1}}),
				Arrays.asList(new Integer[][]{{0}, {1}}, new Integer[][]{{1}, {4}}, new Integer[][]{{0}, {7}}, new Integer[][]{{2, 3}, {3}}, new Integer[][]{{2, 3}, {5}}, new Integer[][]{{0}, {2}}, new Integer[][]{{2, 3}, {5}}, new Integer[][]{{0}, {8}})
		);

		// 设备初始加工时间
		Map<Integer, Integer> machineInitTime = new HashMap<>();
		machineInitTime.put(1, 0);
		machineInitTime.put(2, 0);
		machineInitTime.put(3, 0);
		machineInitTime.put(0, 10);

		// 需要分配的工件(坐标x,坐标y,工时)
		List<Integer[]> needDistributionJob = new ArrayList<>();

		for (int i = 0; i < job.size(); i++) {
			List jobList = job.get(i);
			for (int j = 0; j < jobList.size(); j++) {
				Integer[][] jobDetail = (Integer[][]) jobList.get(j);
				//多设备的选设备
				if (jobDetail[0].length > 1) {
					needDistributionJob.add(new Integer[]{i, j, jobDetail[1][0]});
				}
				//单设备加时长
				else{
					machineInitTime.put(jobDetail[0][0],machineInitTime.get(jobDetail[0][0])+jobDetail[1][0]);
				}
			}
		}
		for (Integer[] a :needDistributionJob){
			System.out.println(Arrays.asList(a));

		}

		// 根据所需时间进行排序(工时倒序)
		needDistributionJob.sort((o1, o2) -> {
			if (o1.length < 3) {
				return 1;
			}
			if (o2.length < 3) {
				return -1;
			}
			if (o2.length < 3 && o1.length < 3) {
				return 1;
			}
			if (o1[2].doubleValue() < o2[2].doubleValue()) {
				return 1;
			} else {
				return -1;
			}
		});

		// 处理需要分配设备的工件(工时从大到小排把最大的排出去)
		for (Integer[] needDistributionJobDeatil : needDistributionJob) {
			Integer[][] detail = job.get(needDistributionJobDeatil[0]).get(needDistributionJobDeatil[1]);
			int minNumber = 0;
			int minTime = 9999999;
			for (int number : detail[0]) {
				if (machineInitTime.get(number) < minTime) {
					minNumber = number;
					minTime = machineInitTime.get(number);
				}
			}
			machineInitTime.put(minNumber, machineInitTime.get(minNumber) + detail[1][0]);
			job.get(needDistributionJobDeatil[0]).set(needDistributionJobDeatil[1], new Integer[][]{{minNumber}, {detail[1][0]}});
		}

		//格式转换
		List<List<Integer[]>> newJob = new ArrayList<>();
		for (int i = 0; i < job.size(); i++) {
			List<Integer[]>  newJobDetail = new ArrayList<>();
			for (Integer[][] t : job.get(i)) {
				newJobDetail.add(new Integer[]{t[0][0], t[1][0]});
			}
			newJob.add(newJobDetail);
		}

		//展示
		for (int i = 0; i < newJob.size(); i++) {
			for (Integer[] t : newJob.get(i)) {
				System.out.println(String.format("工件: %d, 设备: %d, 耗时: %d",
						i + 1, t[0], t[1]));
			}
		}

		System.out.println("亚明生产车间工件分配均衡算法结束！");
		System.out.println("程序用时：" + (new Date().getTime() - startTime.getTime()) / 1000);
	}
}
