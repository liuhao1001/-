package myproject.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import myproject.common.page.PageResult;
import myproject.common.request.RequestSingleParam;
import myproject.common.sysLog.SysLog;
import myproject.common.utils.GloabUtils;
import myproject.common.utils.Result;
import myproject.convert.TousufankuiConvert;
import myproject.entity.TousufankuiEntity;
import myproject.query.TousufankuiQuery;
import myproject.service.TousufankuiService;
import myproject.vo.TousufankuiVO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投诉反馈
 */
@RestController
@RequestMapping("tousufankui")
@Tag(name = "投诉反馈")
@AllArgsConstructor
public class TousufankuiController {
	private final TousufankuiService tousufankuiService;

	@PostMapping("page")
	@Operation(summary = "分页")
	@SysLog(title = "查看投诉反馈分页")
	public Result<PageResult<TousufankuiVO>> page(@RequestBody @Valid TousufankuiQuery query) {
		PageResult<TousufankuiVO> page = tousufankuiService.page(query);
		return Result.ok(page);
	}

	@PostMapping("list")
	@Operation(summary = "列表")
	@SaIgnore
	@SysLog(title = "查看投诉反馈列表")
	public Result<List<TousufankuiVO>> list(@RequestBody @Valid TousufankuiQuery query) {
		List<TousufankuiVO> list = tousufankuiService.queryList(query);
		return Result.ok(list);
	}

	@PostMapping("/info")
	@Operation(summary = "信息")
	@SysLog(title = "查看投诉反馈信息")
	public Result<TousufankuiVO> get(@RequestSingleParam(value = "id") Long id) {
		TousufankuiEntity entity = tousufankuiService.getById(id);
		return Result.ok(TousufankuiConvert.INSTANCE.convert(entity));
	}

	@PostMapping("save")
	@Operation(summary = "保存")
	@SysLog(title = "保存投诉反馈")
	public Result<String> save(@RequestBody TousufankuiVO vo) {
		vo.setUserid(GloabUtils.getUserId());
		vo.setUsername(GloabUtils.getUserInfo().getUsername());
		vo.setStatus("未解决");
		tousufankuiService.save(vo);
		return Result.ok();
	}

	@PostMapping("update")
	@Operation(summary = "修改")
	@SysLog(title = "修改投诉反馈")
	public Result<String> update(@RequestBody @Valid TousufankuiVO vo) {
		tousufankuiService.update(vo);
		return Result.ok();
	}

	@PostMapping("delete")
	@Operation(summary = "删除")
	@SysLog(title = "删除投诉反馈")
	public Result<String> delete(@RequestBody List<Long> idList) {
		tousufankuiService.delete(idList);
		return Result.ok();
	}

	@PostMapping("export")
	@Operation(summary = "导出")
	@SysLog(title = "导出投诉反馈")
	public void export(@RequestBody @Valid TousufankuiQuery query) {
		tousufankuiService.export(query);
	}

	/**
	 * （按值统计）
	 */
	@SaIgnore
	@RequestMapping("/value/{xColumnName}/{yColumnName}")
	public Result value(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName, HttpServletRequest request) {
		Map<String, Object> params = new HashMap<>();
		params.put("xColumn", xColumnName);
		params.put("yColumn", yColumnName);
		LambdaQueryWrapper<TousufankuiEntity> wrapper = Wrappers.lambdaQuery();
		List<Map<String, Object>> result = tousufankuiService.selectValue(params, wrapper);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (Map<String, Object> m : result) {
			for (String k : m.keySet()) {
				if (m.get(k) instanceof Date) {
					m.put(k, sdf.format((Date) m.get(k)));
				}
			}
		}
		return Result.ok(result);
	}

	/**
	 * （按值统计）时间统计类型
	 */
	@SaIgnore
	@RequestMapping("/value/{xColumnName}/{yColumnName}/{timeStatType}")
	public Result valueDay(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName, @PathVariable("timeStatType") String timeStatType, HttpServletRequest request) {
		Map<String, Object> params = new HashMap<>();
		params.put("xColumn", xColumnName);
		params.put("yColumn", yColumnName);
		params.put("timeStatType", timeStatType);
		LambdaQueryWrapper<TousufankuiEntity> wrapper = Wrappers.lambdaQuery();
		List<Map<String, Object>> result = tousufankuiService.selectTimeStatValue(params, wrapper);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (Map<String, Object> m : result) {
			for (String k : m.keySet()) {
				if (m.get(k) instanceof Date) {
					m.put(k, sdf.format((Date) m.get(k)));
				}
			}
		}
		return Result.ok(result);
	}

	/**
	 * 分组统计
	 */
	@SaIgnore
	@RequestMapping("/group/{columnName}")
	public Result group(@PathVariable("columnName") String columnName, HttpServletRequest request) {
		Map<String, Object> params = new HashMap<>();
		params.put("column", columnName);
		LambdaQueryWrapper<TousufankuiEntity> wrapper = Wrappers.lambdaQuery();
		//		wrapper.orderByDesc(TousufankuiEntity::getCount);
		List<Map<String, Object>> result = tousufankuiService.selectGroup(params, wrapper);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (Map<String, Object> m : result) {
			for (String k : m.keySet()) {
				if (m.get(k) instanceof Date) {
					m.put(k, sdf.format((Date) m.get(k)));
				}
			}
		}
		return Result.ok(result);
	}
}