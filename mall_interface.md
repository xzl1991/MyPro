# 积分商城接口

### 修正
* 2016.04.07 增加userInvolvedCrowd协议接口。
* 2016.03.28 增加goodsDetail协议接口。
* 2016.03.17 修改productDetail协议，增加limit返回值。
* 2016.03.04 增加indexPageData和classifyGoods接口
* 2016.02.18 增加crowdColumn和crowdWinList接口
* 2015.12.02 修改createorder协议，在地址类型的订单需要增加address对象参数
* 2015.12.02 删除setdefaddr协议，改调用updateaddr协议。需改updateaddr协议，增加def属性
* 2015.12.02 删除createphoneuser,createlottery,createaddorder三个协议，都调用createorder协议
* 2015.11.27  修改productMain协议，增加action属性6，转入众筹页面
* 2015.11.24 增加crowdDetail协议，修改createOrder协议
* 2015.10.29 修改productDetail协议，增加预加载preload
* 2015.10.28 修改orderList接口
* 2015.10.21 修改createLottery接口，（实际上就是createorder接口的抽奖部分）
* 2015.9.23 重新整理mainProductList接口，重新启用
* 2015.8.27 修改productDetail协议，增加type属性值17. ，修改createorder,input参数type=17时，变成数组
* 2015.8.20 增加getClassify协议，修改newProductMain接口，增加classify属性，增加action属性值
* 2015.8.11 修改createlottery接口bonus属性值，修改ordernewdetailneedpay属性名称及含义，增加addOrderAddr接口
* 2015.8.7 修改 productDetail接口中type属性值，从通用输入页面值从11调整为13
* 2015.8.7 修改productDetail接口，增加input属性，修改createOrder接口，增加input参数，修改createLottery接口返回包结构
* 2015.7.28 修改newProductMain接口，增加action抽奖属性值，增加createLottery接口
* 2015.7.27 修改newProductMain接口，增加action属性，增加getUrl接口
* 2015.7.21 修改orderNewDetail, 增加express属性
* 2015.7.21 修改地址相关接口，改省市区为结构，修改返回包结构
* 2015.7.15 修改productDetail接口，新增smallimg，shortdesc，showprice 3个返回属性
* 2015.7.15 修改newAddress，updateAddress， 将phone属性名称改成pphone
* 2015.7.15 修改orderList接口，增加lastid属性，用于分页读取
* 2015.7.13 修改地址相关接口，统一成addressid，增加setDefAddr接口
* 2015.7.8 修改接口：newAddress,getAddress,updateAddress,增加area属性
* 2015.7.6 新增接口：newAddress，delAddress，updateAddress接口
* 2015.7.6 修改checkin接口
* 2015.7.6 修改createAddrOrder，getAddress接口
* 2015.6.24 增加checkin接口
* 2015.6.24 增加getBanners接口
* 2015.6.19 增加newProductMain接口
* 2015.6.19 修正productDetail接口
* 2015.6.19 修改mainProductList，增加url参数
* 2015.6.19 修改createOrder接口，增加支付参数
* 2015.6.10 orderNewDetail接口增加已取消模板
* 2015.6.10 新增orderNewDetail接口，改版订单详情页
* 2015.6.9  修改orderlist接口，增加tplProduct接口
* 2015.6.2  增加getAddress和createAddrOrder接口
* 2015.6.1  orderdetail接口增加payorderid属性，供再次支付用。
* 2015.6.1  createorder接口增加paystatus，payorderid属性，供支付用。
* 2015.5.28 checkphone接口增加productid上传
* 2015.5.27 productDetail接口增加confirm属性
* 2015.5.27 增加checkPhone，createPhoneUser接口，所有请求包头增加uid，将ua变成authcode
* 2015.5.21 增加mainProductList接口（首页商品列表）
* 2015.5.17 修改productDetail中的type属性含义，增加button属性
* 2015.5.15 orderdetail返回包增加desc属性
* 2015.5.14 productDetail返回包增加type属性
* 2015.5.13 修正orderdetail和productDetail请求参数
* 2015.5.12 productDetail接口增加thirdparturl属性


## 1.用户相关接口
### 1.1 获取用户信息
__method__
* `getUserInfo`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` | String | 用户id |
| `authcode` | String | 用户token |
| `uid` | String | 设备id |

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `points` | int | 用户积分|
| `level` | int | 用户级别|

__example__

```JavaScript
返回: {"points":4731,"level":0}
```

### 1.2 签到接口
__method__
* `checkin`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `addpoint` |int |增加积分数量|
| `point` |int |总积分数量|
| `msg` |String |用户提示信息|


### 1.3 请求下发手机验证码（手机注册第三方）
__method__
* `checkPhone`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `productid` |String |商品id|
| `cphone` |String |下发验证码手机号|

__result__

* “ok”

## 2. 订单相关接口
### 2.1 创建订单接口
__method__
* `createOrder`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `productid` |String |商品id|
| `hbauthcode` |String |支付用authcode|
| `hbuserid` |String |支付用userid|
| `input` |input或input数组 |输入表单内容,只有商品类型为13，17的才需要|
| `num` |int |订购数量|
| `address` |address结构<br>`addressid`:地址id<br>`province`:{"id":省份id}<br>`city`:{"id":城市id}<br>`area`:{"id":区县id}<br>`pphone`:"电话"<br>`address`:地址<br>`name`:姓名<br>`postcode`:邮编<br>`def`:是否为默认地址 1-是  0-否 |地址类才有|

```JavaScript
input Json结构：
type=13时 {"输入项1":"输入值1","输入项2":"输入值2",….}
type=17时 [{"输入项1":"输入值1","输入项2":"输入值2",….},
           {"输入项1":"输入值1","输入项2":"输入值2",….},
           ...
          ]
输入项定义由商品详情下发
```

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `orderid` |String |订单id|
| `message` |String |显示信息|
| `orderurl` |String |订单详情url|
| `paystatus` |int | 支付状态 0：未支付  1：已支付|
| `payorderid` |String |支付订单id，启动nativeapi支付用。|
| `paydesc` |String |支付用标题|
| `paysubdesc` |String |支付用描述|

__example__

```JavaScript
{"orderid":"1431316329231","message":"恭喜，您成功兑换顺丰优选100元优惠券, 兑换码:aaaaaaaaa1","orderurl":null}
```


### 2.2 createAddrOrder（删除，调用createorder）

### 2.3 createPhoneUser（删除，调用createorder）

### 2.4 获取订单列表
__method__
* `orderList`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `last` |String |最后一个商品的id，分页用|
| `type` |int |订单类型<br>0：全部订单<br>1：代处理订单<br>2：成功订单<br>3：失败订单<br>4：搜索订单|
| `key` |String |type为4的时候有效，搜索关键字|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `result` | order数组|20条order记录，如果不到20条，说明已经到了最后了。|

```JavaScript
order结构
    orderid String 订单ID
    userid String 用户ID
    createtime String 创建时间
    price String 价格字符串
    title String 产品标题
    img String 产品图片地址
    status String 状态字符串
    color String描述状态类名称
```
__example__

```JavaScript
返回：{"result":[{"orderid":"4338348104888","userid":"372306073653696","createtime":"2015年06月09日","price":"￥0.15","title":"连咖啡20元代金券","img":"http://cdn.rsscc.cn/guanggao/img/mall/h1-common-goods-lyan-20.png","color":2,"status":"成功购买"}
```

### 2.5 orderDetail（删除，调用orderNewDetail）

### 2.6 获取订单详情
__method__
* `orderNewDetail`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `orderid` |String |订单id|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `orderid` |String |订单ID|
| `createtime` |String |创建时间|
| `statusstr` |String |状态显示串|
| `productid` |int |产品id|
| `stattpl` |String |状态模板（类名称）|
| `orderprice` |float |订单价格|
| `msgtpl` |int |信息区模板<br>0：不显示<br>1：有效期模板<br>2：文字模板<br>3：地址模板|
| `msg` |String |信息区内容|
| `expdate` |String |失效时间|
| `img` |String |图片url|
| `title` |String |商品标题|
| `shotdesc` |String |商品短描述|
| `price` |String |商品价格|
| `note` |String |使用说明|
| `action` |int | 后续动作<br>1：需要支付<br>0：无动作<br>2：补全地址|
| `payprice` |float |支付价格|
| `payorderid` |String |支付订单ID|
| `express` |express结构<br>`company`：快递公司名称<br>`tracking`: 快递单号<br>`url`: 获取快递状态url |快递信息|
| `num` |int |购买数量|
| `points` |int |积分单价|
| `ptotal` |int |积分总价|
| `money` |float |现金单价|
| `mtotal` |int |现金总价|

### 2.7 给地址订单增加地址信息
__method__
* `addOrderAddr`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `hbauthcode` |String |支付用authcode|
| `hbuserid` |String |支付用userid|
| `orderid` |String |订单id|
| `address` |address结构<br>`addressid`:地址id<br>`province`:{"id":省份id}<br>`city`:{"id":城市id}<br>`area`:{"id":区县id}<br>`pphone`:"电话"<br>`address`:地址<br>`name`:姓名<br>`postcode`:邮编<br>`def`:是否为默认地址 1-是  0-否 |地址类才有|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `addressid` |int |地址id|

__example__


## 3. 商品相关接口

### 3.11 首页内容（菜单、首屏的商品列表）

__method__

* `indexPageData`

__params__

* 无

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `topmenu` | Array | 大的分类菜单，示例：天天特价、最新上架、积分兑换、一元夺宝、主图故事 |
| `topgoods`  | Array | 推荐位置（大分类菜单下面的四个突出、明显的位置） |
| `menu` | Array | 所有的商品类别（可展开的那个菜单） |
| `goods` | Array | 首屏的商品列表（显示在所有的商品类别菜单下） |

> * topmenu 结构
>   * `groupId` 组id
>   * `title` 商品标题,
>   * `img` 图片地址
>   * `url` 如果有这个url，则点击后转入这个url，不进入商品详情
>   * `classify` 频道
>   * `action` 动作类型    
>        * 0：转入普通商品详情   
>        * 1：转入模板详情
>        * 2：直接跳转第三方
>        * 3：需要转换跳转地址， 
>        * 4: 转入刮刮卡抽奖
>        * 5：跳转入商品频道
>        * 6: 转入众筹页面
>        * 7：跳转入众筹频道
>        * 8: 转入新版频道页
>        * 9: 转入新版商品详情

> * topgoods 结构
>   * `productid` 商品id
>   * `title` 商品标题,
>   * `img` 图片地址
>   * `url` 如果有这个url，则点击后转入这个url，不进入商品详情
>   * `classify` 频道
>   * `action` 动作类型    
>        * 0：转入普通商品详情   
>        * 1：转入模板详情
>        * 2：直接跳转第三方
>        * 3：需要转换跳转地址， 
>        * 4: 转入刮刮卡抽奖
>        * 5：跳转入商品频道
>        * 6: 转入众筹页面
>        * 7：跳转入众筹频道
>        * 8: 转入新版频道页
>        * 9: 转入新版商品详情

> * menu 结构
>   * `groupId` 组id
>   * `title` 商品标题,
>   * `url` 如果有这个url，则点击后转入这个url，不进入商品详情
>   * `classify` 频道
>   * `action` 动作类型    
>        * 0：转入普通商品详情   
>        * 1：转入模板详情
>        * 2：直接跳转第三方
>        * 3：需要转换跳转地址， 
>        * 4: 转入刮刮卡抽奖
>        * 5：跳转入商品频道
>        * 6: 转入众筹页面
>        * 7：跳转入众筹频道
>        * 8: 转入新版频道页
>        * 9: 转入新版商品详情

> * goods 结构
>   * `productid` 商品id
>   * `title` 商品标题,
>   * `img` 图片地址
>   * `points` 商品所需积分，示例：20
>   * `money` 商品所需价钱（单位：元），示例：5
>   * `url` 如果有这个url，则点击后转入这个url，不进入商品详情
>   * `classify` 频道
>   * `action` 动作类型    
>        * 0：转入普通商品详情   
>        * 1：转入模板详情
>        * 2：直接跳转第三方
>        * 3：需要转换跳转地址， 
>        * 4: 转入刮刮卡抽奖
>        * 5：跳转入商品频道
>        * 6: 转入众筹页面
>        * 7：跳转入众筹频道
>        * 8: 转入新版频道页
>        * 9: 转入新版商品详情


### 3.12 商品类别接口（获取指定类别下的商品列表，与原`getClassify`接口类似）

__method__

* `classifyGoods`

__params__

| 属性名      | 类型    |  描述  |
| :----      | :-----  | :---- |
| `groupId`  | int  | 频道Id |

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `title` | String |  频道名 |
| `goods` | Array  | 首屏的商品列表（显示在所有的商品类别菜单下） |

> * goods 结构
>   * `productid` 商品id
>   * `title` 商品标题,
>   * `img` 图片地址
>   * `points` 商品所需积分，示例：20
>   * `money` 商品所需价钱（单位：元），示例：5
>   * `url` 如果有这个url，则点击后转入这个url，不进入商品详情
>   * `classify` 频道
>   * `action` 动作类型    
>        * 0：转入普通商品详情   
>        * 1：转入模板详情
>        * 2：直接跳转第三方
>        * 3：需要转换跳转地址， 
>        * 4: 转入刮刮卡抽奖
>        * 5：跳转入商品频道
>        * 6: 转入众筹页面
>        * 7：跳转入众筹频道
>        * 8: 转入新版频道页
>        * 9: 转入新版商品详情


### 3.13 商品详情接口（新）

__method__

* `goodsDetail`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `productid` |int |商品id|
| `imei` |String |手机唯一吗|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `stat` |int |状态<br>0：正常兑换<br>1：已结束<br>2：未开始<br>3：已兑完<br>4：今日已兑完|
| `curtime` |long |当前时间|   ----状态为2的时候有效
| `begintime` |long |开始时间|  ----状态为2的时候有效
| `type` |int |兑换类型<br>1--直接调用创建订单 <br>2--转入输入手机号页面<br>3--转入输入地址页面（预留）   <br>9--点击跳转第三方链接（thridparturl） <br>13—输入一些信息页面 <br>17-批量输入模板|
| `thirdparturl` |String |当type=9的时候，点击兑换，直接跳转到该网址.|
| `confirm` |String |用户点击兑换后的确认信息|
| `mprice` |float |如果有支付的话，价格|
| `input` |input结构数组<br>[{“code”:xxx,”name”:xxxx,”desc”:xxxxx},……]<br>code: 上传时的名称<br>name: 是展示名称<br>desc: 是未输入时展示内容|如果type=13，17则有这个对象。|
| `preload`  |String |预加载url，如果有，则后台进行预加载|
| `limit` |int |每个订单可购数量<br>0为不可购买<br>大于1时则限制当前订单可购数量<br>等于1时不多选|
| `productid` |int |产品ID|
| `smallimg` |String |小图标地址（用于选地址界面）|
| `shortdesc` |String |短描述（用于选地址界面）|
| `title` |String |页面标题|
| `img` |String |图片url|
| `desc` |String |商品描述|
| `tags` |Array |商品标签|
| `relevance` |Object |相关商品 *** |
| `detail` |Array |商品的图文详情|
| `detailurl` |String |URL，用于配置为商品的图文详情，如果有此字段，界面模板将忽略 `detail` 字段|
| `rules` |Array |说明信息（规则）|
| `recommend` |Array |猜你喜欢（推荐商品列表）*** |
| `points` |int |商品所需积分，示例：20|
| `money`  |int |商品所需价钱（单位：元），示例：5|
| `button` |String |按钮文字（底部主按钮文字，示例：立即兑换）|

> * tags 结构（字符串）
>   * String 示例 tag: [ "第三方包邮", "正品保证", "原装进口" ]

> * relevance 结构
>   * `productid` 商品id
>   * `groupId` 组id
>   * `title` 商品标题,
>   * `img` 图片地址
>   * `points` 商品所需积分，示例：20
>   * `money` 商品所需价钱（单位：元），示例：5
>   * `button` 按钮文字（示例：直接购买）
>   * `url` 如果有这个url，则点击后转入这个url，不进入商品详情
>   * `classify` 频道
>   * `action` 动作类型    
>        * 0：转入普通商品详情   
>        * 1：转入模板详情
>        * 2：直接跳转第三方
>        * 3：需要转换跳转地址， 
>        * 4: 转入刮刮卡抽奖
>        * 5：跳转入商品频道
>        * 6: 转入众筹页面
>        * 7：跳转入众筹频道
>        * 8: 转入新版频道页
>        * 9: 转入新版商品详情

> * detail 结构
>   * `name` 文字内容
>   * `img` 图片

> * rules 结构
>   * `name` 标题，示例：价格说明
>   * `content` 内容，示例：原价199元，使用优惠券仅需60元

> * recommend 结构
>   * `productid` 商品id
>   * `groupId` 组id
>   * `title` 商品标题,
>   * `img` 图片地址
>   * `points` 商品所需积分，示例：20
>   * `money` 商品所需价钱（单位：元），示例：5
>   * `url` 如果有这个url，则点击后转入这个url，不进入商品详情
>   * `classify` 频道
>   * `action` 动作类型    
>        * 0：转入普通商品详情   
>        * 1：转入模板详情
>        * 2：直接跳转第三方
>        * 3：需要转换跳转地址， 
>        * 4: 转入刮刮卡抽奖
>        * 5：跳转入商品频道
>        * 6: 转入众筹页面
>        * 7：跳转入众筹频道
>        * 8: 转入新版频道页
>        * 9: 转入新版商品详情


### 3.1 获取首页商品列表

__method__

* `mainProductList`

__params__

* 无

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `focus` |product结构数组| 推荐位3个商品，对应在置顶结构中的左，右上，右下三个位置。如果不为空，则保证3条记录。|
| `groups` |group结构数组 |首页商品组信息|

>	* group结构
>		* `title` String 组标题
>		* `products` product结构数组 该组中所有商品信息
>	* product结构
>		* `productid` 商品id
>		* `title` 商品标题,
>		* `detail` 商品描述,
>		* `stateicon: 引用的类名称,
>		* `pprice`: 价格字串
>		* `img`: 图片地址
>		* `url`：如果有这个url，则点击后转入这个url，不进入商品详情
>		* `action`: 动作类型  	
>                       * 0：转入普通商品详情   
>			* 1：转入模板详情
>			* 2：直接跳转第三方
>			* 3：需要转换跳转地址， 
>			* 4: 转入刮刮卡抽奖
>			* 5：跳转入商品频道
>			* 6: 转入众筹页面
>                       * 7：跳转入众筹频道
>                       * 8: 转入新版频道页


### 3.2 获取商品详情

__method__

* `productDetail`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `productid` |int |商品id|
| `imei` |String |手机唯一吗|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `productid` |int |产品ID|
| `img` |String |图片url|
| `desc` |String |商品描述|
| `pprice` |int |积分价格|
| `stat` |int |状态<br>0：正常兑换<br>1：已结束<br>2：未开始<br>3：已兑完<br>4：今日已兑完|
| `button` |String |按钮文字|
| `curtime` |long |当前时间|   ----状态为2的时候有效
| `begintime` |long |开始时间|  ----状态为2的时候有效
| `type` |int |兑换类型<br>1--直接调用创建订单 <br>2--转入输入手机号页面<br>3--转入输入地址页面（预留）   <br>9--点击跳转第三方链接（thridparturl） <br>13—输入一些信息页面 <br>17-批量输入模板|
| `thirdparturl` |String |当type=9的时候，点击兑换，直接跳转到该网址.|
| `confirm` |String |用户点击兑换后的确认信息|
| `title` |String |页面标题|
| `mprice` |float |如果有支付的话，价格|
| `smallimg` |String |小图标地址|
| `shortdesc` |String |短描述|
| `showprice` |String |显示价格|
| `input` |input结构数组<br>[{“code”:xxx,”name”:xxxx,”desc”:xxxxx},……]<br>code: 上传时的名称<br>name: 是展示名称<br>desc: 是未输入时展示内容|如果type=13，17则有这个对象。|
| `preload`  |String |预加载url，如果有，则后台进行预加载|
| `limit` |int |每个订单可购数量<br>0为不可购买<br>大于1时则限制当前订单可购数量<br>等于1时不多选|

__example__

```JavaScript
{"productid":1,"img":"http://images.rsscc.com/ad/ticketad/running/images/pic1.png","desc":"顺丰优选299-100优惠券","exchangedesc":null,"userange":null,"usedesc":null,"pprice":1,"stat":0,"curtime":null,"begintime":null}
```

### 3.3 获取中间页模版
__method__
* `tplProduct`

__params__
* 无

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `tpl` |String |网页模板|
| `title` |String |网页标题|

__example__

### 3.4 获取首页焦点图
__method__
* `getBanners`

__params__
* 无

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
|| banner数组|banner结构<br>title：页面标题<br>img：图片地址<br>url：跳转地址<br>productid：商品id|

__example__

### 3.5 newProductMain(删除， 使用productmain接口）


### 3.5 获取京东链接
__method__
* `getUrl`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `productid` |int |商品id|
| `imei` |int |移动设备国际识别码|
| `p` |int |用于识别 APP 的自定义的标识|
| `uuid` |String |设备唯一吗。|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `url` |String |跳转的url|

__example__

### 3.6 获取频道内容
__method__
* `getClassify`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `classify` |String |频道名称|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `title` |String |频道名称|
| `products` |product数组<br>product结构<br>`productid`:商品id<br>`title`:商品标题,<br>`detail` 商品描述,<br>`stateicon`: 引用的类名称,<br>`pprice`: 价格字串<br>`img`: 图片地址<br>`url`：跳转第三方的地址<br>`action`: 动作类型<br>  	* 0：转入普通商品详情   <br>	* 1：转入模板详情<br>	* 2：直接跳转第三方<br>	* 3：需要转换跳转地址， <br>	* 4: 转入刮刮卡抽奖<br>	* 5：跳转入商品频道<br>	* 6: 转入众筹页面 ||

__example__


## 4. 地址相关接口
### 4.1 获取用户地址列表
__method__
* `getAddress`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
* `userid` String 用户id|
* `authcode` String 用户token|
* `uid` String 设备id|
* `hbauthcode` String 支付用authcode|
* `hbuserid` String 支付用userid|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| |address数组|address 结构<br>`addressid`: 地址id<br>`userid`：用户id<br>`province`：省份 结构{“id”:”xxxx”,”name”:”xxxxxx”}<br>`city`：城市 结构{“id”:”xxxx”,”name”:”xxxxxx”}<br>`area`: 区县 结构{“id”:”xxxx”,”name”:”xxxxxx”}<br>`phone`：电话<br>`address`：地址<br>`name`:姓名<br>`postcode`：邮编|

__example__

### 4.2 修改用户地址信息
__method__
* `updateAddress`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `hbauthcode` |String |支付用authcode|
| `hbuserid` |String |支付用userid|
| `addressid` |int |地址id|
| `province` |int |省份id|
| `city` |int |城市id|
| `area` |int |区县id|
| `pphone` |String |电话|
| `address` |String |地址|
| `name` |String |姓名|
| `postcode` |String |邮编|
| `def` |int |是否为默认地址<br>1-是<br>0-否|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `addressid` |int |地址id|

__example__

### 4.3 删除用户地址
__method__
* `delAddress`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `hbauthcode` |String |支付用authcode|
| `hbuserid` |String |支付用userid|
| `addressid` |String |地址id|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `addressid` |int |地址id|

__example__


### 4.4 新增用户地址
__method__
* `newAddress`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `hbauthcode` |String |支付用authcode|
| `hbuserid` |String |支付用userid|
| `province` |int |省份id|
| `city` |int |城市id|
| `area` |int |区县id|
| `pphone` |String |电话|
| `address` |String |地址|
| `name` |String |姓名|
| `postcode` |String |邮编|
| `def` |int |是否为默认地址<br>1-是<br>  0-否|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `addressid` |int |地址id|

__example__

### 4.5 setDefAddr（删除）

## 4. 抽奖相关接口
### 4.1 createLottery（删除，调用createOrder）

## 5. 众筹相关接口
### 5.1   众筹详情接口

__method__
* `crowdDetail`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `productid` |String |商品id|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `img` |String |众筹大图|
| `hint` |String |文字|
| `purchased` |String数组 |本人购买code|
| `price` |float |价格|
| `showprice` |String |展示价格|
| `playerlist` |购买记录数组<br>购买记录结构<br>`ordered` String 订单id    为了今后下拉用<br>`phone` String 电话号码<br>`count` int 购买数量<br>`time` String 支付时间|当前商品购买记录，返回10条记录|
| `totalcount` |int |商品可购总数|
| `remaincount` |int |剩余可购数量|
| `rulestpl` |String |获奖规则|
| `stat` |int |状态<br>0-待开奖<br>1-正常购买中<br>2-已开奖|
| `winner` |获奖结构<br>`productid` int 商品id<br>`wincode` int 获奖code<br>`userid` String 获奖人id<br>`phone` String 获奖人电话 <br>`paytime` 获奖支付时间 |获奖人员，开奖后才有|


__example__

	private int productid;
	private int wincode;
	private String userid;
	private String phone;
	private String paytime;

### 5.2   众筹获奖结果

__method__

* `crowdResult`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `productid` |String |商品id|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `wincode` | Number | 本期幸运号码 |
| `anumber` | Number | 例：6，即截止该奖品最后购买时间点前最后6条全站参与记录的参与时间 |
| `avalue` | Number | 数值A 的值 |
| `bvalue` | Number | 数值B 的值 |
| `timelist` | 秒杀时间结构<br>time String 时间<br>tvalue Number 时间对应的数值<br>account String 参与帐号 | 秒杀时间列表 |


### 5.3   众筹频道页面

__method__

* `crowdColumn`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `productid` |String |商品id|

__result__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `banner` | Array | 轮播图列表 |
| `winner` | Array | 轮播条列表 |
| `product` | Array | 一元夺宝商品列表 |

> * banner 结构
>   * `productid` 商品id
>   * `title` 商品标题,
>   * `img`: 图片地址
>   * `url`：如果有这个url，则点击后转入这个url，不进入商品详情
>   * `action`: 动作类型    
>        * 0：转入普通商品详情   
>        * 1：转入模板详情
>        * 2：直接跳转第三方
>        * 3：需要转换跳转地址， 
>        * 4: 转入刮刮卡抽奖
>        * 5：跳转入商品频道
>        * 6: 转入众筹页面
>        * 7：跳转入众筹频道
>        * 8: 转入新版频道页
>        * 9: 转入新版商品详情

> * product 结构
>   * `title` 商品标题,
>   * `progress`：开奖进度，示例：Number 0~100之间
>   * `productid` 商品id
>   * `img`: 图片地址
>   * `url`：如果有这个url，则点击后转入这个url，不进入商品详情
>   * `action`: 动作类型    
>        * 0：转入普通商品详情   
>        * 1：转入模板详情
>        * 2：直接跳转第三方
>        * 3：需要转换跳转地址， 
>        * 4: 转入刮刮卡抽奖
>        * 5：跳转入商品频道
>        * 6: 转入众筹页面
>        * 7：跳转入众筹频道
>        * 8: 转入新版频道页
>        * 9: 转入新版商品详情

> * winner 结构
>   * `phone` 手机号，示例：136\****9900
>   * `title` 商品标题，示例：[第1期] iPhone 6S
>   * `productid` 商品id
>   * `img`: 图片地址
>   * `url`：如果有这个url，则点击后转入这个url，不进入商品详情
>   * `action`: 动作类型    
>        * 0：转入普通商品详情   
>        * 1：转入模板详情
>        * 2：直接跳转第三方
>        * 3：需要转换跳转地址， 
>        * 4: 转入刮刮卡抽奖
>        * 5：跳转入商品频道
>        * 6: 转入众筹页面
>        * 7：跳转入众筹频道
>        * 8: 转入新版频道页
>        * 9: 转入新版商品详情

### 5.4   众筹开奖记录

__method__

* `crowdWinList`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `productid` |String |商品id|

__result__

>   * `title` 商品标题，示例：[第1期] iPhone 6S
>   * `phone` 手机号，示例：136\****9900
>   * `wincode` 幸运号码，示例：10000329
>   * `winnerbuynum` int 获奖者购买次数
>   * `time` 揭晓时间，示例：2016-02-16 08:05:22
>   * `productid` 商品id
>   * `img`: 图片地址
>   * `url`：如果有这个url，则点击后转入这个url，不进入商品详情
>   * `action`: 动作类型    
>        * 0：转入普通商品详情   
>        * 1：转入模板详情
>        * 2：直接跳转第三方
>        * 3：需要转换跳转地址， 
>        * 4: 转入刮刮卡抽奖
>        * 5：跳转入商品频道
>        * 6: 转入众筹页面
>        * 7：跳转入众筹频道
>        * 8: 转入新版频道页
>        * 9: 转入新版商品详情

### 5.5   我的参与

__method__

* `userInvolvedCrowd`

__params__

| 属性名        | 类型   |  描述  |
| :---- | :-----  | :---- |
| `userid` |String |用户id|
| `authcode` |String |用户token|
| `uid` |String |设备id|
| `last` |String |最后一个商品对应的orderid，分页用|
| `limit` |int |每页显示条数|

__result__

__result__

>   * `orderid` String 订单ID
>   * `title` String 商品标题，示例：[第1期] iPhone 6S
>   * `phone` String 手机号，示例：136\****9900
>   * `productid` int 商品id
>   * `url`：String 如果有这个url，则点击后转入这个url，不进入商品详情
>   * `img`: String 图片地址
>   * `time` String 揭晓时间，示例：2016-02-16 08:05:22
>   * `wincode` String 幸运号码，示例：10000329
>   * `winnerbuynum` int 获奖者购买次数
>   * `buynum` int 我的购买次数
>   * `winner` int  1表示自己中奖，0表示自己未中奖
>   * `sort` int 5表示商品还未开奖，4表示已开奖
>   * `totalcount` int 商品可购总数
>   * `remaincount` int 剩余可购数量
>   * `progress`：int 开奖进度，示例：Number 0~100之间
>   * `action`:int  为6：转入众筹页面  

