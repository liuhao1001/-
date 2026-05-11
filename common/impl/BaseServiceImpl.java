package myproject.common.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import myproject.common.query.Query;
import myproject.common.utils.BaseService;


/**
 * 基础服务类，所有Service都要继承
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {

	/**
	 * 获取分页对象
	 *
	 * @param query 分页参数
	 */
	protected IPage<T> getPage(Query query) {
		Page<T> page = new Page<>(query.getPage(), query.getLimit());

		// 排序
		if (StrUtil.isNotBlank(query.getOrder())) {
			if (query.isAsc()) {
				return page.addOrder(OrderItem.asc(query.getOrder()));
			} else {
				return page.addOrder(OrderItem.desc(query.getOrder()));
			}
		}

		return page;
	}
}