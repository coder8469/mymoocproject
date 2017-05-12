package com.mall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.StudentInfo;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.dao.*;
import com.mall.pojo.*;
import com.mall.service.IOrderService;
import com.mall.util.BigdecimalUtil;
import com.mall.util.DateTimeUtil;
import com.mall.util.FileUtil;
import com.mall.util.PropertiesUtil;
import com.mall.vo.OrderItemVo;
import com.mall.vo.OrderProductVo;
import com.mall.vo.OrderVo;
import com.mall.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2017-5-5.
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse pay(Integer userId,Long orderId,String path){
        Map resultMap = Maps.newHashMap();
        Order order = orderMapper.selectOrderByOrderId(userId,orderId);
        if(order == null){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        resultMap.put("orderNo",String.valueOf(order.getOrderNo()));
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
        String subject = new StringBuilder().append("mall商城扫码支付，订单号：")
                .append(order.getOrderNo().toString()).toString();
        log.info("subject 商城扫码支付，订单号：{}",subject);
        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (必填) 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
        //String authCode = "用户自己的支付宝付款码"; // 条码示例，286648048691290423
        // (可选，根据需要决定是否使用) 订单可打折金额，可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折
        // 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】
        //        String discountableAmount = "1.00"; //
        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0.0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
        String body = "购买商品3件共20.00元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = new StringBuilder().append("订单").append(order.getOrderNo()
                .toString()).append("购买的商品总共 ").append(totalAmount).append("元").toString();

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        String providerId = "2088100200300400500";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);
        // 支付超时，线下扫码交易定义为5分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        List<OrderItem> orderItemList = orderItemMapper.selectOrderByOrderNoUserId(order.getOrderNo(),userId);
        for(OrderItem orderItem : orderItemList){
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString()
                    ,orderItem.getProductName()
                    ,BigdecimalUtil.mul( orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue() ).longValue()
                    ,orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods);
        }
        //String appAuthToken = "应用授权令牌";//根据真实值填写
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                File folder = new File(path);
                if(!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + "/qr-%s.png",response.getOutTradeNo());//创建文件路径
                log.info("qrPath:" + qrPath);
                String qrFileName = String.format("/qr-%s.png",response.getOutTradeNo());//创建文件名
                log.info("qrFileName:" + qrFileName);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(path,qrFileName);
                log.info("targetFile:" + targetFile);
                try {
                    FileUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    log.info("上传二维码失败" + e);
                }
                String qrUrl = new StringBuilder().append(PropertiesUtil
                        .getProperty("ftp.server.http.prefix").toString())
                        .append(targetFile.getName()).toString();
                log.info("qrUrl {}",qrUrl);
                resultMap.put("qrUrl",qrUrl);
                return ServerResponse.createBySccessMessage(resultMap);

            case FAILED:
                log.error("支付宝支付失败!!!");
                return ServerResponse.createByErrorMessage("支付宝支付失败!!!");

            case UNKNOWN:
                log.error("系统异常，订单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，订单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    public ServerResponse alipayCallBack(Map<String,String> params){
        //从回调的中获取数据
        String orderNo = params.get("out_trade_no");
        log.info("alipayCallBack orderNo : {}",orderNo);
        String tradeStatus = params.get("trade_status");
        log.info("alipayCallBack tradeStatus :{} ",tradeStatus);
        String tradeNo = params.get("trade_no");
        log.info("alipayCallBack tradeNo : {}",tradeNo);
        //查询数据库中订单
        Order order = orderMapper.selectOrderByOrderNo(Long.parseLong(orderNo));
        if(order == null){
            //不是本商城的订单直接返回错误信息
            return ServerResponse.createByErrorMessage("不是本商城的订单，忽略回调信息");
        }
        if(order.getStatus() >= Const.OrderStatus.PAID.getCode()){
            //判断订单状态是已支付，忽略
            return ServerResponse.createBySccessMessage("支付宝重复调用");
        }
        if(Const.AlipayCallBack.TRADE_STATTUS_TRADE_SUCCESS.equals(tradeStatus)){
            //支付宝付款成功，更新订单状态
            log.info("order.getStatus() ：{}",order.getStatus());
            order.setStatus( Const.OrderStatus.PAID.getCode() );
            log.info("Const.OrderStatus.PAID.getCode() : {}",Const.OrderStatus.PAID.getCode());
            log.info("order.getStatus() ：{}",order.getStatus());
            log.info("params.get(\"gmt_payment\") ：{}",params.get("gmt_payment"));
            order.setPaymentTime(DateTimeUtil.strToDate( params.get("gmt_payment") ));
            log.info("order.getPaymentTime() ：{}",order.getPaymentTime());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        //更新订单详情表
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatForm.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySccess();
    }

    public ServerResponse queryOrderPay( Integer userId, Long orderNo){
        Order order = orderMapper.selectOrderByOrderId(userId,orderNo);
        if(order == null){
            log.info("order.getStatus(){}",order.getStatus());
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        if( order.getStatus() >= Const.OrderStatus.PAID.getCode() ){
            return ServerResponse.createBySccess();
        }
        return ServerResponse.createByError();
    }

    /**
     * 创建订单
     * @param userId
     * @param shippingId
     * @return
     */
    public ServerResponse createOrder(Integer userId,Integer shippingId){
        //先查询购物车中是否为空,获取购物车已选中的商品
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse = this.getCartOrderItem( userId,cartList );
        if( !serverResponse.isSuccess() ){
           return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>)serverResponse.getData();
        if( CollectionUtils.isEmpty(orderItemList) ){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        BigDecimal payment = countOrderTotalPrice( orderItemList );  //计算购物车总价
        Order order = this.assembleOrder(userId,payment,shippingId); //生成订单信息
        if( order == null ){
            return ServerResponse.createByErrorMessage("订单生成失败");
        }
        for( OrderItem orderItem : orderItemList ){
            orderItem.setOrderNo( order.getOrderNo() );//设置这些订单条目从属于哪个订单
        }
        //订单信息批量插入数据
        orderItemMapper.batchOrderInsert( orderItemList );
        this.reduceProductStock( orderItemList );  //减少库存
        this.clearCart( cartList );  //清空购物车
        OrderVo orderVo = assembleOrderVo( order,orderItemList );
        return ServerResponse.createBySccessMessage( orderVo );
    }

    public ServerResponse cancle( Integer userId,Long orderNo){
        Order order = orderMapper.selectOrderByOrderId(userId,orderNo);
        if( order == null ){
            return ServerResponse.createByErrorMessage("此订单不存在，无法取消");
        }
        Order updateOrder = new Order();
        updateOrder.setId( order.getId() );
        updateOrder.setStatus( Const.OrderStatus.CANCLE.getCode() );
        int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(rowCount > 0){
            return ServerResponse.createBySccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse getOrderCart(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        ServerResponse serverResponse = this.getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>)serverResponse.getData();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        BigDecimal payment = new BigDecimal("0");
        for( OrderItem orderItem : orderItemList ){
            payment = BigdecimalUtil.add( payment.doubleValue(),orderItem.getTotalPrice().doubleValue() );
            orderItemVoList.add( assembleOrderItemVo( orderItem ) );
        }
        orderProductVo.setProductTotatlPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost( PropertiesUtil.getProperty("ftp.server.http.prefix") );
        return ServerResponse.createBySccessMessage( orderProductVo );
    }

    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo( order.getOrderNo() );
        orderVo.setPayment( order.getPayment() );
        orderVo.setPaymentType( order.getPaymentType() );
        orderVo.setPaymentTypeDesc( Const.PayType.valueOfCode( order.getPaymentType() ).getValue() );
        orderVo.setPostage( order.getPostage() );
        orderVo.setStatus( order.getStatus() );
        orderVo.setStatusDesc( Const.OrderStatus.valueOfCode( order.getStatus() ).getValue() );
        orderVo.setShippingId( order.getShippingId() );
        Shipping shipping = shippingMapper.selectByPrimaryKey( order.getShippingId() );
        if( shipping != null ){
            orderVo.setReceiveName( shipping.getReceiverName() );
            orderVo.setShippingVo( assembleShippingVo(shipping) );
        }
        orderVo.setPaymentTime( DateTimeUtil.dateToStr( order.getPaymentTime() ) );
        orderVo.setSendTime( DateTimeUtil.dateToStr( order.getSendTime()) );
        orderVo.setCloseTime( DateTimeUtil.dateToStr( order.getCloseTime() ));
        orderVo.setCreateTime( DateTimeUtil.dateToStr( order.getCreateTime()) );
        orderVo.setImageHost( PropertiesUtil.getProperty("ftp.server.http.prefix") );
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList( orderItemVoList );
        return orderVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo( orderItem.getOrderNo() );
        orderItemVo.setProductId( orderItem.getProductId() );
        orderItemVo.setProductName( orderItem.getProductName() );
        orderItemVo.setCurrentUnitPrice( orderItem.getCurrentUnitPrice() );
        orderItemVo.setQuantity( orderItem.getQuantity() );
        orderItemVo.setTotalPrice( orderItem.getTotalPrice() );
        orderItemVo.setCreateTime( DateTimeUtil.dateToStr( orderItem.getCreateTime() ) );
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo( Shipping shipping ){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName( shipping.getReceiverName() );
        shippingVo.setReceiverAddress( shipping.getReceiverAddress() );
        shippingVo.setReceiverCity( shipping.getReceiverCity() );
        shippingVo.setReceiverDistrict( shipping.getReceiverDistrict() );
        shippingVo.setReceiverMobile( shipping.getReceiverMobile() );
        shippingVo.setReceiverPhone( shipping.getReceiverPhone() );
        shippingVo.setReceiverZip( shipping.getReceiverZip() );
        return shippingVo;
    }

    private void clearCart(List<Cart> cartList){
        for( Cart cart : cartList ){
            cartMapper.deleteByPrimaryKey( cart.getId() );
        }
    }
    private void reduceProductStock(List<OrderItem> orderItemList){
        for( OrderItem orderItem : orderItemList ){
            Product product = productMapper.selectByPrimaryKey( orderItem.getProductId() );
            product.setStock( product.getStock() - orderItem.getQuantity() );
            productMapper.updateByPrimaryKeySelective( product );
        }
    }
    private Order assembleOrder(Integer userId,BigDecimal payment,Integer shippingId){
        Order order = new Order();
        long orderNo = this.generateOrderNo();
        order.setUserId( userId );
        order.setOrderNo( orderNo );
        order.setPostage(0);  //运邮费
        order.setStatus( Const.OrderStatus.NO_PAY.getCode() );
        order.setPaymentType( Const.PayType.ALIPAYONLINE.getCode() ); //支付方式
        order.setPayment( payment ); //支付金额
        order.setShippingId( shippingId );//发货地址
        int rowCount = orderMapper.insert( order );
        if( rowCount > 0 ){
            return order;
        }
        return  null;
    }
    private Long generateOrderNo(){
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }
    private BigDecimal countOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");
        for( OrderItem orderItem : orderItemList ){
            payment = BigdecimalUtil.add( orderItem.getTotalPrice().doubleValue(),payment.doubleValue() );
        }
        return payment;
    }

    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        if(CollectionUtils.isEmpty( cartList )){
            log.info("cartList {} 购物车为空",CollectionUtils.isEmpty( cartList ));
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        List<OrderItem> orderItemList = Lists.newArrayList();
        for( Cart cart : cartList ){
            //遍历购物车中的产品
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey( cart.getProductId() );
            log.info("product {}",product.getId());
            if( product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode() ){
                log.info("商品状态 {}",product.getStatus());
                log.info("商品状态 {}",Const.ProductStatusEnum.ON_SALE.getCode());
                //产品不在售卖状态，返回提示信息
                return ServerResponse.createByErrorMessage("改产品已经下架");
            }
            if( product.getStock() < cart.getQuantity() ){
                //产品库存少于购买数量，返回提示信息
                return ServerResponse.createByErrorMessage(product.getName() + "库存不足，" + "当前库存为 " + product.getStock());
            }
            //设置定单单条产品条目的详细信息
            orderItem.setUserId( userId );
            orderItem.setProductId( product.getId() );
            orderItem.setProductName( product.getName() );
            orderItem.setCurrentUnitPrice( product.getPrice() );
            orderItem.setProductImage( product.getMainImage() );
            orderItem.setQuantity( cart.getQuantity() );
            orderItem.setTotalPrice( BigdecimalUtil.mul( cart.getQuantity().doubleValue(),product.getPrice().doubleValue() ) );
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySccessMessage( orderItemList );
    }

    /**
     * 获取订单详细
     * @param userId
     * @param orderNo
     * @return
     */
    public ServerResponse<OrderVo> getOrderDetail(Integer userId,Long orderNo){
        Order order = orderMapper.selectOrderByOrderId(userId,orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("没有找到该订单");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectOrderByOrderNoUserId(orderNo,userId);
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySccessMessage( orderVo );
    }

    /**
     *  普通会员用户获取所有订单
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getOrderList(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.getOrderListByUserId(userId);
        List<OrderVo> orderVoList = assembleOrderVoList(userId,orderList);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySccessMessage( pageResult );
    }

    /**  backend
     *  管理员用户获取所有用户订单
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getOrderList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.getOrderListManager();
        List<OrderVo> orderVoList = assembleOrderVoList(null,orderList);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySccessMessage( pageResult );
    }

    /**
     *  管理员获取订单详细
     * @param orderNo
     * @return
     */
    public ServerResponse<OrderVo> orderDetail(Long orderNo){
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        if( order != null ){
            List<OrderItem> orderItemList = orderItemMapper.selectOrderByOrderNo( order.getOrderNo() );
            OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySccessMessage(orderVo);
        }
        return ServerResponse.createByErrorMessage("此订单不存在");
    }

    public ServerResponse<PageInfo> searchOrder(Long orderNo,int pageNum,int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        if( order != null ){
            List<OrderItem> orderItemList = orderItemMapper.selectOrderByOrderNo( order.getOrderNo() );
            OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
            PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
            pageInfo.setList( Lists.newArrayList(orderVo) );
            return ServerResponse.createBySccessMessage(pageInfo);
        }
        return ServerResponse.createByErrorMessage("此订单不存在");
    }

    public ServerResponse<String> sendGoods(Long orderNo){
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        if( order != null ){
            //订单存在
            if (order.getStatus() == Const.OrderStatus.PAID.getCode()) {
                //订单已支付完成
                order.setStatus( Const.OrderStatus.SHIPPED.getCode() );
                order.setSendTime( new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySccessMessage("您的订单已经发货");
            }
        }
        return ServerResponse.createByErrorMessage("此订单不存在");
    }

    private List<OrderVo> assembleOrderVoList(Integer userId,List<Order> orderList){
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orderList) {
            List<OrderItem> orderItemList = Lists.newArrayList();
            if (userId == null) {
                //如果是管理员用户，则不需要userId
                orderItemList = orderItemMapper.selectOrderByOrderNo(order.getOrderNo());
            }else {
                orderItemList = orderItemMapper.selectOrderByOrderNoUserId(order.getOrderNo(), userId);
            }
            OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }
}
