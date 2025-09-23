package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.attr.AttrValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.tomcat.util.modeler.AttributeInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private RoomAttrValueService roomAttrValueService;

    @Autowired
    private RoomFacilityService roomFacilityService;

    @Autowired
    private RoomLabelService roomLabelService;

    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;

    @Autowired
    private RoomLeaseTermService roomLeaseTermService;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private RoomAttrValueMapper roomAttrValueMapper;

    @Autowired
    private RoomFacilityMapper roomFacilityMapper;

    @Autowired
    private RoomLabelMapper roomLabelMapper;

    @Autowired
    private RoomPaymentTypeMapper roomPaymentTypeMapper;

    @Autowired
    private RoomLeaseTermMapper roomLeaseTermMapper;

    @Override
    public void saveOrUpdateRoom(RoomSubmitVo roomSubmitVo) {
        boolean isUpdate = roomSubmitVo.getId() != null;
        super.saveOrUpdate(roomSubmitVo);

        if (isUpdate) {
            //删除图片
            LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
            graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphQueryWrapper.eq(GraphInfo::getItemId, roomSubmitVo.getId());
            graphInfoService.remove(graphQueryWrapper);

            //删除属性信息
            LambdaQueryWrapper<RoomAttrValue> roomAttrValueQueryWrapper = new LambdaQueryWrapper<>();
            roomAttrValueQueryWrapper.eq(RoomAttrValue::getRoomId, roomSubmitVo.getId());
            roomAttrValueService.remove(roomAttrValueQueryWrapper);

            //删除配套信息
            LambdaQueryWrapper<RoomFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(RoomFacility::getRoomId, roomSubmitVo.getId());
            roomFacilityService.remove(facilityQueryWrapper);

            //删除标签信息
            LambdaQueryWrapper<RoomLabel> roomLabelQueryWrapper = new LambdaQueryWrapper<>();
            roomLabelQueryWrapper.eq(RoomLabel::getRoomId, roomSubmitVo.getId());
            roomLabelService.remove(roomLabelQueryWrapper);

            //删除支付方式
            LambdaQueryWrapper<RoomPaymentType> paymentTypeQueryWrapper = new LambdaQueryWrapper<>();
            paymentTypeQueryWrapper.eq(RoomPaymentType::getRoomId, roomSubmitVo.getId());
            roomPaymentTypeService.remove(paymentTypeQueryWrapper);

            //删除可选租期
            LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermQueryWrapper = new LambdaQueryWrapper<>();
            roomLeaseTermQueryWrapper.eq(RoomLeaseTerm::getRoomId, roomSubmitVo.getId());
            roomLeaseTermService.remove(roomLeaseTermQueryWrapper);
        }

        //插入图片
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        if(!CollectionUtils.isEmpty(graphVoList)){
            ArrayList<GraphInfo> graphInfoList = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemType(ItemType.ROOM);
                graphInfo.setItemId(roomSubmitVo.getId());
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfoList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfoList);
        }

        //插入属性
        List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
        if(!CollectionUtils.isEmpty(attrValueIds)){
            List<RoomAttrValue> roomAttrValueList = new ArrayList<>();
            for (Long attrValueId : attrValueIds) {
                RoomAttrValue roomAttrValue = RoomAttrValue.builder().roomId(roomSubmitVo.getId()).attrValueId(attrValueId).build();
                roomAttrValueList.add(roomAttrValue);
            }
            roomAttrValueService.saveBatch(roomAttrValueList);
        }

        //插入配套
        List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();
        if(!CollectionUtils.isEmpty(facilityInfoIds)){
            List<RoomFacility> roomFacilityList = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                RoomFacility roomFacility = RoomFacility.builder().roomId(roomSubmitVo.getId()).facilityId(facilityInfoId).build();
                roomFacilityList.add(roomFacility);
            }
            roomFacilityService.saveBatch(roomFacilityList);
        }

        //插入标签
        List<Long> labelInfoIds = roomSubmitVo.getLabelInfoIds();
        if(!CollectionUtils.isEmpty(labelInfoIds)){
            List<RoomLabel> roomLabelList = new ArrayList<>();
            for (Long labelInfoId : labelInfoIds) {
                RoomLabel roomLabel = RoomLabel.builder().roomId(roomSubmitVo.getId()).labelId(labelInfoId).build();
                roomLabelList.add(roomLabel);
            }
            roomLabelService.saveBatch(roomLabelList);
        }

        //插入支付方式
        List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
        if(!CollectionUtils.isEmpty(paymentTypeIds)){
            List<RoomPaymentType> roomPaymentTypeList = new ArrayList<>();
            for (Long paymentTypeId : paymentTypeIds) {
                RoomPaymentType roomPaymentType = RoomPaymentType.builder().paymentTypeId(paymentTypeId).build();
                roomPaymentTypeList.add(roomPaymentType);
            }
            roomPaymentTypeService.saveBatch(roomPaymentTypeList);
        }

        //插入租期
        List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();
        if(!CollectionUtils.isEmpty(leaseTermIds)){
            List<RoomLeaseTerm> roomLeaseTermList = new ArrayList<>();
            for (Long leaseTermId : leaseTermIds) {
                RoomLeaseTerm roomLeaseTerm = RoomLeaseTerm.builder().leaseTermId(leaseTermId).build();
                roomLeaseTermList.add(roomLeaseTerm);
            }
            roomLeaseTermService.saveBatch(roomLeaseTermList);
        }
    }

    @Override
    public IPage<RoomItemVo> pageRoomItemByQuery(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
        return roomInfoMapper.pageRoomItemByQuery(page,queryVo);
    }

    @Override
    public RoomDetailVo getRoomDetailById(Long id) {
        //查询房间信息
        RoomInfo roomInfo = roomInfoMapper.selectById(id);
        //查找所属公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(roomInfo.getApartmentId());
        //查找图片信息
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, id);
        //查找属性
        List<AttrValueVo> attrValueVoList = roomAttrValueMapper.selectListByRoomId(id);
        //查找配套
        List<FacilityInfo> facilityInfoList = roomFacilityMapper.selectListByRoomId(id);
        //查找标签
        List<LabelInfo> labelInfoList = roomLabelMapper.selectListByRoomId(id);
        //查找可选支付方式
        List<PaymentType> paymentTypeList = roomPaymentTypeMapper.selectListByRoomId(id);
        //查找可选租期
        List<LeaseTerm> roomLeaseTerm = roomLeaseTermMapper.selectListByRoomId(id);

        RoomDetailVo roomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo, roomDetailVo);
        roomDetailVo.setGraphVoList(graphVoList);
        roomDetailVo.setAttrValueVoList(attrValueVoList);
        roomDetailVo.setFacilityInfoList(facilityInfoList);
        roomDetailVo.setLabelInfoList(labelInfoList);
        roomDetailVo.setPaymentTypeList(paymentTypeList);
        roomDetailVo.setLeaseTermList(roomLeaseTerm);

        return roomDetailVo;
    }

    @Override
    public void removeRoomById(Long id) {
        //删除房间
        super.removeById(id);
        //删除图片
        LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
        graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
        graphQueryWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphQueryWrapper);

        //删除属性信息
        LambdaQueryWrapper<RoomAttrValue> roomAttrValueQueryWrapper = new LambdaQueryWrapper<>();
        roomAttrValueQueryWrapper.eq(RoomAttrValue::getRoomId, id);
        roomAttrValueService.remove(roomAttrValueQueryWrapper);

        //删除配套信息
        LambdaQueryWrapper<RoomFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
        facilityQueryWrapper.eq(RoomFacility::getRoomId, id);
        roomFacilityService.remove(facilityQueryWrapper);

        //删除标签信息
        LambdaQueryWrapper<RoomLabel> roomLabelQueryWrapper = new LambdaQueryWrapper<>();
        roomLabelQueryWrapper.eq(RoomLabel::getRoomId, id);
        roomLabelService.remove(roomLabelQueryWrapper);

        //删除支付方式
        LambdaQueryWrapper<RoomPaymentType> paymentTypeQueryWrapper = new LambdaQueryWrapper<>();
        paymentTypeQueryWrapper.eq(RoomPaymentType::getRoomId, id);
        roomPaymentTypeService.remove(paymentTypeQueryWrapper);

        //删除可选租期
        LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermQueryWrapper = new LambdaQueryWrapper<>();
        roomLeaseTermQueryWrapper.eq(RoomLeaseTerm::getRoomId, id);
        roomLeaseTermService.remove(roomLeaseTermQueryWrapper);
    }


}




