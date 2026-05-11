package myproject.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import myproject.common.page.PageResult;
import myproject.common.request.RequestSingleParam;
import myproject.common.sysLog.SysLog;
import myproject.common.utils.Result;
import myproject.convert.AboutusConvert;
import myproject.entity.AboutusEntity;
import myproject.query.AboutusQuery;
import myproject.service.AboutusService;
import myproject.vo.AboutusVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 关于我们
 */
@RestController
@RequestMapping("aboutus")
@Tag(name = "关于我们")
@AllArgsConstructor
public class AboutusController {
	private final AboutusService aboutusService;

	@PostMapping("page")
	@Operation(summary = "分页")
	@SysLog(title = "查看关于我们分页")
	public Result<PageResult<AboutusVO>> page(@RequestBody @Valid AboutusQuery query) {
		PageResult<AboutusVO> page = aboutusService.page(query);
		return Result.ok(page);
	}

	@PostMapping("list")
	@Operation(summary = "列表")
	@SaIgnore
	@SysLog(title = "查看关于我们列表")
	public Result<List<AboutusVO>> list(@RequestBody @Valid AboutusQuery query) {
		List<AboutusVO> list = aboutusService.queryList(query);
		return Result.ok(list);
	}

	@PostMapping("/info")
	@Operation(summary = "信息")
	@SysLog(title = "查看关于我们信息")
	public Result<AboutusVO> get(@RequestSingleParam(value = "id") Long id) {
		AboutusEntity entity = aboutusService.getById(id);
		return Result.ok(AboutusConvert.INSTANCE.convert(entity));
	}

	@PostMapping("save")
	@Operation(summary = "保存")
	@SysLog(title = "保存关于我们信息")
	public Result<String> save(@RequestBody AboutusVO vo) {
		aboutusService.save(vo);
		return Result.ok();
	}

	@PostMapping("update")
	@Operation(summary = "修改")
	@SysLog(title = "修改关于我们信息")
	public Result<String> update(@RequestBody @Valid AboutusVO vo) {
		aboutusService.update(vo);
		return Result.ok();
	}

	@PostMapping("delete")
	@Operation(summary = "删除")
	@SysLog(title = "删除关于我们信息")
	public Result<String> delete(@RequestBody List<Long> idList) {
		aboutusService.delete(idList);
		return Result.ok();
	}

	@PostMapping("export")
	@Operation(summary = "导出")
	@SysLog(title = "导出关于我们信息")
	public void export(@RequestBody @Valid AboutusQuery query) {
		aboutusService.export(query);
	}
}