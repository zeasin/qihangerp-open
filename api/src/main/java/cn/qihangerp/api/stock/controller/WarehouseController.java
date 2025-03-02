package cn.qihangerp.api.stock.controller;

import cn.qihangerp.api.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.stock.domain.WmsWarehouse;
import cn.qihangerp.module.stock.domain.WmsWarehousePosition;
import cn.qihangerp.module.stock.service.WmsWarehousePositionService;
import cn.qihangerp.module.stock.service.WmsWarehouseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/erp-api/warehouse")
public class WarehouseController extends BaseController {
    private final WmsWarehouseService warehouseService;
    private final WmsWarehousePositionService positionService;
    @GetMapping("/list")
    public TableDataInfo list(WmsWarehouse bo)
    {
        LambdaQueryWrapper<WmsWarehouse> qw = new LambdaQueryWrapper<WmsWarehouse>()
                .eq(bo.getStatus()!=null,WmsWarehouse::getStatus, bo.getStatus())
                .like(StringUtils.hasText(bo.getNumber()),WmsWarehouse::getNumber,bo.getNumber())
                .like(StringUtils.hasText(bo.getName()),WmsWarehouse::getName,bo.getName())
                ;
        List<WmsWarehouse> wmsWarehouses = warehouseService.list(qw);
        return getDataTable(wmsWarehouses);
    }

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(warehouseService.getById(id));
    }
    @PostMapping
    public AjaxResult add(@RequestBody WmsWarehouse warehouse)
    {
        warehouse.setCreateBy(getUsername());
        warehouse.setCreateTime(new Date());
        boolean save = warehouseService.save(warehouse);
        if(save){
            WmsWarehousePosition position = new WmsWarehousePosition();
            position.setWarehouseId(warehouse.getId());
            position.setParentId(0);
            position.setParentId1(0);
            position.setParentId2(0);
            position.setNumber(warehouse.getNumber());
            position.setName(warehouse.getName());
            position.setIsDelete(0);
            position.setAddress(warehouse.getAddress());
            position.setRemark(warehouse.getRemark());
            position.setCreateBy(getUsername());
            position.setCreateTime(new Date());
            positionService.save(position);
        }
        return AjaxResult.success();
    }
    @PutMapping
    public AjaxResult edit(@RequestBody WmsWarehouse warehouse)
    {
        warehouse.setUpdateBy(getUsername());
        warehouse.setUpdateTime(new Date());
        return toAjax(warehouseService.updateById(warehouse));
    }
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(warehouseService.removeBatchByIds(Arrays.stream(ids).toList()));
    }

    @GetMapping("/position/list")
    public TableDataInfo positionList(Long warehouseId)
    {
        LambdaQueryWrapper<WmsWarehousePosition> qw = new LambdaQueryWrapper<WmsWarehousePosition>()
                .eq(WmsWarehousePosition::getWarehouseId,warehouseId)
                ;
        List<WmsWarehousePosition> list = positionService.list(qw);
        return getDataTable(list);
    }
    @GetMapping("/position/search")
    public TableDataInfo searchPosition(Long warehouseId,String number)
    {
        LambdaQueryWrapper<WmsWarehousePosition> qw = new LambdaQueryWrapper<WmsWarehousePosition>()
                .eq(WmsWarehousePosition::getWarehouseId,warehouseId)
                .like(WmsWarehousePosition::getNumber,number)
                ;
        List<WmsWarehousePosition> list = positionService.list(qw);
        return getDataTable(list);
    }


    @PostMapping("/position")
    public AjaxResult positionAdd(@RequestBody WmsWarehousePosition position) {
        position.setCreateBy(getUsername());
        position.setCreateTime(new Date());
        position.setParentId1(0);
        position.setParentId2(0);
        positionService.save(position);

        return AjaxResult.success();
    }

    @GetMapping(value = "/position/{id}")
    public AjaxResult getPositionInfo(@PathVariable("id") Long id)
    {
        return success(positionService.getById(id));
    }

    @PutMapping("/position")
    public AjaxResult positionEdit(@RequestBody WmsWarehousePosition position)
    {
        position.setUpdateBy(getUsername());
        position.setUpdateTime(new Date());
        return toAjax(positionService.updateById(position));
    }
    @DeleteMapping("/position/{ids}")
    public AjaxResult positionRemove(@PathVariable Long[] ids)
    {
        return toAjax(positionService.removeBatchByIds(Arrays.stream(ids).toList()));
    }


}
