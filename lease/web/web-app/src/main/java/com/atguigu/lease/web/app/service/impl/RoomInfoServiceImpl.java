package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.context.LoginUserContext;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.AttrValueService;
import com.atguigu.lease.web.app.service.BrowsingHistoryService;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.attr.AttrValueVo;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.atguigu.lease.web.app.vo.room.RoomItemVo;
import com.atguigu.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private AttrValueMapper attrValueMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    @Autowired
    private BrowsingHistoryService browsingHistoryService;

    @Override
    public IPage<RoomItemVo> pageRoomItemByQuery(Page<RoomItemVo> page, RoomQueryVo queryVo) {
        return roomInfoMapper.pageRoomInfoByQuery(page,queryVo);
    }

    @Override
    public RoomDetailVo getRoomDetailById(Long id) {

        //查RoomInfo
        RoomInfo roomInfo = roomInfoMapper.selectById(id);
        if(roomInfo == null){
            return null;
        }

        //查所属公寓信息
        ApartmentItemVo apartmentItemVo = apartmentInfoService.selectApartmentItemVoById(roomInfo.getApartmentId());

        //查图片
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM,id);

        //查属性
        List<AttrValueVo> attrValueVoList = attrValueMapper.selectListByRoomId(id);

        //查配套
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByRoomId(id);

        //查标签
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByRoomId(id);

        //查可选支付
        List<PaymentType> paymentTypeList = paymentTypeMapper.selectListByRoomId(id);

        //查可选租期
        List<LeaseTerm> leaseTermList = leaseTermMapper.selectListByRoomId(id);

        //查房间杂费
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(id);

        //查询房间入住状态
        LambdaQueryWrapper<LeaseAgreement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseAgreement::getRoomId,id);
        queryWrapper.in(LeaseAgreement::getStatus,LeaseStatus.SIGNED,LeaseStatus.WITHDRAWING);
        Long singedCount = leaseAgreementMapper.selectCount(queryWrapper);

        RoomDetailVo appRoomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo, appRoomDetailVo);
        appRoomDetailVo.setIsDelete(roomInfo.getIsDeleted() == 1);
        appRoomDetailVo.setIsCheckIn(singedCount > 0);

        appRoomDetailVo.setApartmentItemVo(apartmentItemVo);
        appRoomDetailVo.setGraphVoList(graphVoList);
        appRoomDetailVo.setAttrValueVoList(attrValueVoList);
        appRoomDetailVo.setFacilityInfoList(facilityInfoList);
        appRoomDetailVo.setLabelInfoList(labelInfoList);
        appRoomDetailVo.setPaymentTypeList(paymentTypeList);
        appRoomDetailVo.setFeeValueVoList(feeValueVoList);
        appRoomDetailVo.setLeaseTermList(leaseTermList);

        browsingHistoryService.saveHistory(LoginUserContext.getLoginUser().getUserId(), id);
        return appRoomDetailVo;
    }

    @Override
    public IPage<RoomItemVo> pageItemByApartmentId(IPage<RoomItemVo> page, Long id) {
        return roomInfoMapper.pageItemByApartmentId(page,id);
    }
}




