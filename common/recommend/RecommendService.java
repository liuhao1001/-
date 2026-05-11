package myproject.common.recommend;

import cn.hutool.core.util.NumberUtil;
import lombok.AllArgsConstructor;
import myproject.query.StoreupQuery;
import myproject.service.StoreupService;
import myproject.vo.StoreupVO;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class RecommendService {
	private final StoreupService storeupService;

	/**
	 * @return java.util.List<java.lang.Integer>
	 * @title: ItemCFRecommend
	 * @Description //基于物品的推荐
	 * @Param [itemId, list]
	 **/
	//example
	//	public  List<Integer> recommend(Integer itemId, List<RelateDTO> list) {
	//		//按物品分组
	//		Map<Integer, List<RelateDTO>>  itemMap=list.stream().collect(Collectors.groupingBy(RelateDTO::getItemId));
	//		//获取其他物品与当前物品的关系值
	//		Map<Integer,Double>  itemDisMap = CoreMath.computeNeighbor(itemId, itemMap,1);
	//		//获取关系最近物品
	//		double maxValue=Collections.max(itemDisMap.values());
	//		return itemDisMap.entrySet().stream().filter(e->e.getValue()==maxValue).map(Map.Entry::getKey).collect(Collectors.toList());
	//	}
	public List<Long> ItemCFRecommend(Long itemId, List<Long> itemIds, String tablename) {
		StoreupQuery storeupQuery = new StoreupQuery();
		storeupQuery.setTablename(tablename);
		List<RelateDTO> list = getBehaviorDate(storeupQuery, itemIds);
		//按物品分组
		Map<Long, List<RelateDTO>> itemMap = list.stream().collect(Collectors.groupingBy(RelateDTO::getItemId));
		//如果行为数据不足，直接返回补充数据
		if (!itemMap.containsKey(itemId) || itemMap.size() < 2) {
			return itemIds.stream().limit(20).collect(Collectors.toList());
		}
		//获取其他物品与当前物品的关系值
		Map<Long, Double> itemDisMap = computeNeighbor(itemId, itemMap, 1);
		//如果没有相关物品，返回补充数据
		if (itemDisMap.isEmpty()) {
			return itemIds.stream().limit(20).collect(Collectors.toList());
		}
		//获取关系最近物品
		double maxValue = Collections.max(itemDisMap.values());
		List<Long> items = itemDisMap.entrySet().stream().filter(e -> e.getValue() == maxValue).map(Map.Entry::getKey).collect(Collectors.toList());
		if (items.size() >= 20) {
			return items;
		}
		items = new ArrayList<>(items);
		List<Long> finalList = items;
		List<Long> supplement = itemIds.stream()
				.filter(r -> finalList.stream().noneMatch(p -> p.equals(r)))
				.limit(20 - items.size()).collect(Collectors.toList());
		items.addAll(supplement);
		return items;
	}

	private List<RelateDTO> getBehaviorDate(StoreupQuery storeupQuery, List<Long> itemIds) {
		List<StoreupVO> storeupVOS = storeupService.queryList(storeupQuery);
		List<RelateDTO> relateDTOS = new ArrayList<>();
		for (StoreupVO storeupVO : storeupVOS) {
			if (!itemIds.contains(storeupVO.getRefid())) {
				continue;
			}
			RelateDTO relateDTO = new RelateDTO();
			relateDTO.setUseId(storeupVO.getUserid());
			relateDTO.setItemId(storeupVO.getRefid());
			relateDTO.setIndex(NumberUtil.toDouble(storeupVO.getType()));
			relateDTOS.add(relateDTO);
		}
		return relateDTOS;
	}

	/**
	 * @return java.util.List<java.lang.Integer>
	 * @title: UserCFRecommend
	 * @Description //基于用户的推荐
	 * @Param [userId, list]
	 **/
	//example
	//	public  List<Integer> recommend(Integer userId, List<RelateDTO> list) {
	//		//按用户分组
	//		Map<Integer, List<RelateDTO>>  userMap=list.stream().collect(Collectors.groupingBy(RelateDTO::getUseId));
	//		//获取其他用户与当前用户的关系值
	//		Map<Integer,Double>  userDisMap = CoreMath.computeNeighbor(userId, userMap,0);
	//		//获取关系最近的用户
	//		double maxValue=Collections.max(userDisMap.values());
	//		Set<Integer> userIds=userDisMap.entrySet().stream().filter(e->e.getValue()==maxValue).map(Map.Entry::getKey).collect(Collectors.toSet());
	//		//取关系最近的用户
	//		Integer nearestUserId = userIds.stream().findAny().orElse(null);
	//		if(nearestUserId==null){
	//			return Collections.emptyList();
	//		}
	//		//最近邻用户看过电影列表
	//		List<Integer>  neighborItems = userMap.get(nearestUserId).stream().map(RelateDTO::getItemId).collect(Collectors.toList());
	//		//指定用户看过电影列表
	//		List<Integer>  userItems  = userMap.get(userId).stream().map(RelateDTO::getItemId).collect(Collectors.toList());
	//		//找到最近邻看过，但是该用户没看过的电影
	//		neighborItems.removeAll(userItems);
	//		return neighborItems;
	//	}
	public List<Long> UserCFRecommend(Long userId, List<Long> itemIds, String tablename) {
		try {
			StoreupQuery storeupQuery = new StoreupQuery();
			storeupQuery.setTablename(tablename);
			List<RelateDTO> list = getBehaviorDate(storeupQuery, itemIds);
			//按用户分组
			Map<Long, List<RelateDTO>> userMap = list.stream().collect(Collectors.groupingBy(RelateDTO::getUseId));
			//如果没有用户行为数据，则直接返回补充数据
			if (!userMap.containsKey(userId)) {
				List<Long> supplement = itemIds.stream()
						.limit(20).collect(Collectors.toList());
				return supplement;
			}
			//获取其他用户与当前用户的关系值
			Map<Long, Double> userDisMap = computeNeighbor(userId, userMap, 0);
			//获取关系最近的用户
			if (userDisMap.isEmpty()) {
				List<Long> supplement = itemIds.stream()
						.limit(20).collect(Collectors.toList());
				return supplement;
			}
			double maxValue = Collections.max(userDisMap.values());
			Set<Long> userIds = userDisMap.entrySet().stream().filter(e -> e.getValue() == maxValue).map(Map.Entry::getKey).collect(Collectors.toSet());
			//取关系最近的用户
			Long nearestUserId = userIds.stream().findAny().orElse(null);
			if (nearestUserId == null) {
				return Collections.emptyList();
			}
			List<Long> neighborItems = userMap.get(nearestUserId).stream().map(RelateDTO::getItemId).collect(Collectors.toList());
			List<Long> userItems = userMap.get(userId).stream().map(RelateDTO::getItemId).collect(Collectors.toList());
			neighborItems.removeAll(userItems);
			if (neighborItems.size() >= 20) {
				return neighborItems;
			}
			neighborItems = new ArrayList<>(neighborItems);
			List<Long> finalList = neighborItems;
			List<Long> supplement = itemIds.stream()
					.filter(r -> finalList.stream().noneMatch(p -> p.equals(r)))
					.limit(Math.max(20 - neighborItems.size(), 0)).collect(Collectors.toList());
			neighborItems.addAll(supplement);
			return neighborItems;
		} catch (Exception e) {
			return itemIds;
		}

	}


	/**
	 * 计算相关系数并排序
	 *
	 * @param key
	 * @param map
	 * @return Map<Integer, Double>
	 */
	public Map<Long, Double> computeNeighbor(Long key, Map<Long, List<RelateDTO>> map, int type) {
		Map<Long, Double> distMap = new TreeMap<>();
		List<RelateDTO> userItems = map.get(key);
		//如果key不在map中，返回空Map
		if (userItems == null) {
			return distMap;
		}
		map.forEach((k, v) -> {
			//排除此用户
			if (!k.equals(key)) {
				//关系系数
				double coefficient = relateDist(v, userItems, type);
				//关系距离
				//   double distance=Math.abs(coefficient);
				distMap.put(k, coefficient);
			}
		});
		return distMap;
	}


	/**
	 * 计算两个序列间的相关系数
	 *
	 * @param xList
	 * @param yList
	 * @param type  类型0基于用户推荐 1基于物品推荐
	 * @return double
	 */
	private double relateDist(List<RelateDTO> xList, List<RelateDTO> yList, int type) {
		List<Double> xs = Lists.newArrayList();
		List<Double> ys = Lists.newArrayList();
		//增加空校验
		if(xList==null||yList==null){
			return 0D;
		}
		xList.forEach(x -> {
			yList.forEach(y -> {
				if (type == 0) {
					if (x.getItemId().equals(y.getItemId())) {
						xs.add(x.getIndex());
						ys.add(y.getIndex());
					}
				} else {
					if (x.getUseId().equals(y.getUseId())) {
						xs.add(x.getIndex());
						ys.add(y.getIndex());
					}
				}
			});
		});
		return getRelate(xs, ys);
	}

	/**
	 * 方法描述: 皮尔森（pearson）相关系数计算
	 *
	 * @param xs x集合
	 * @param ys y集合
	 * @Return {@link double}
	 * @author tarzan
	 */
	public double getRelate(List<Double> xs, List<Double> ys) {
		int n = xs.size();
		//至少有两个元素
		if (n < 2) {
			return 0D;
		}
		double Ex = xs.stream().mapToDouble(x -> x).sum();
		double Ey = ys.stream().mapToDouble(y -> y).sum();
		double Ex2 = xs.stream().mapToDouble(x -> Math.pow(x, 2)).sum();
		double Ey2 = ys.stream().mapToDouble(y -> Math.pow(y, 2)).sum();
		double Exy = IntStream.range(0, n).mapToDouble(i -> xs.get(i) * ys.get(i)).sum();
		double numerator = Exy - Ex * Ey / n;
		double denominator = Math.sqrt((Ex2 - Math.pow(Ex, 2) / n) * (Ey2 - Math.pow(Ey, 2) / n));
		if (denominator == 0) {
			return 0D;
		}
		return numerator / denominator;
	}
}