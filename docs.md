# 插件和mod通信

## 插件方面

1. 插件的pom.xml 需要在dependency中添加<br>
```html
<dependency>
   <groupId>io.netty</groupId>
   <artifactId>netty-all</artifactId>
   <version>4.1.111.Final</version>
</dependency>
```
2. 然后构建maven，刷新依赖
3. 在CowCannon主插件类中定义一个channel(需要与mod的channel对应，new ResourceLocation(SeasonShop.MODID, "main") == "modid:main")
   -  public static final String channel = "season_shop:main";
- 插件处新建一个用于处理发包的类:这里以PricePacket为例：
```java
public class PricePacket {

    private String itemId;
    private double price;
   
    public PricePacket(String itemId, double price) {
        this.itemId = itemId;
        this.price = price;
    }
   
    public static void handle(byte[] message) {
        PricePacket pricePacket = read(message);
   
        System.out.println("Item ID: " + pricePacket.getItemId());
        System.out.println("Item Price: " + pricePacket.getPrice());
    }
   
    public String getItemId() {
        return itemId;
    }
   
    public double getPrice() {
        return price;
    }
   
    private static PricePacket read(byte[] array) {
        ByteBuf buf = Unpooled.wrappedBuffer(array); // 使用给定的字节数组创建 ByteBuf 缓冲区
          short version = buf.readUnsignedByte(); // 读取一个无符号字节作为版本号
        
        if (version == 1) {
          int itemIdLength = buf.readInt(); // 读取 itemId 的长度（4 个字节）
          byte[] itemIdBytes = new byte[itemIdLength]; // 根据长度创建字节数组
          buf.readBytes(itemIdBytes); // 读取 itemId 的字节数据
          String itemId = new String(itemIdBytes, StandardCharsets.UTF_8); // 将字节数据转换为字符串，使用 UTF-8 编码
          double price = buf.readDouble(); // 读取价格（8 个字节）
          return new PricePacket(itemId, price); // 创建并返回新的 PricePacket 对象
        } else {
            throw new IllegalArgumentException("Unknown packet version: " + version);
        }
    }
   
    public void send(Player player) {
        byte[] bytes = itemId.getBytes(StandardCharsets.UTF_8);
        // Calculate buffer size: 1 byte for version, 4 bytes for item ID length, item ID bytes length, 8 bytes for price
        ByteBuf buf = Unpooled.buffer(1 + 4 + bytes.length + 8);
           
        buf.writeByte(1); // 写入版本号
        buf.writeInt(bytes.length); // 写入Item ID的长度
        buf.writeBytes(bytes); // 写入Item ID的字节码
        buf.writeDouble(price); // 写入price
   
        player.sendPluginMessage(CowCannon.getInstance(), CowCannon.channel, buf.array());
    }
}
```
4. 注意
    - 在 read 方法中：
      - 创建 ByteBuf：将字节数组包装为 ByteBuf 缓冲区以便读取数据。
      - 读取版本号：确保数据格式的一致性，通过检查版本号判断数据的结构。
      - 读取 itemId 长度和字节数组：根据 itemId 的长度读取字节数据，并将其转换为字符串。
      - 读取价格：按照预期格式读取价格数据。
      - 返回 PricePacket 对象：根据读取的数据创建并返回新的 PricePacket 对象。如果版本号未知，则抛出异常。
    - 在 send 方法中：
      - 将 itemId 转换为字节数组：使用 StandardCharsets.UTF_8 编码格式将 itemId 字符串转换为字节数组。
          - 计算缓冲区大小：根据消息内容计算所需缓冲区的大小，包括：
              - 1 个字节的版本号
              - 4 个字节的 itemId 长度
              - itemId 字节数组的实际长度
              - 8 个字节的价格（double 类型）
          - 写入数据：
            - 写入版本号（1 个字节）
            - 写入 itemId 长度（4 个字节）
            - 写入 itemId 字节数组
            - 写入价格（8 个字节）
          - 发送插件消息：调用 player.sendPluginMessage 方法，将缓冲区的数据作为消息发送到Forge。
    - 总结
        - read 方法：确保按照正确的顺序读取数据（长度信息、字节数组），并使用正确的编码格式将字节数组解码为字符串。
        - send 方法：确保按照正确的顺序写入数据（版本号、长度信息、字节数组、价格），并根据消息内容计算缓冲区的大小。

## Mod方面
1. 创建一个NetworkHandler
```java
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SeasonShop.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static SimpleChannel getNetwork() {
        return CHANNEL;
    }
}
```
2. 在你的mod主文件中加上
```java
// The value here should match an entry in the META-INF/mods.toml file
@Mod(YourMod.MODID)
@Mod.EventBusSubscriber(modid = YourMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class YourMod {
    public static final String MODID = "mod_id";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static YourMod instance;

    public YourMod() {
        instance = this;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static YourMod getInstance() {
        return instance;
    }

    // 必须注册这三个Channel 否则服务端无法识别
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.getNetwork().registerMessage(1, PricesPacket.class, PricesPacket::encode, PricesPacket::decode, PricesPacket::handle);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        NetworkHandler.getNetwork().registerMessage(1, PricesPacket.class, PricesPacket::encode, PricesPacket::decode, PricesPacket::handle);
    }

    @SubscribeEvent
    public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        NetworkHandler.getNetwork().registerMessage(1, PricesPacket.class, PricesPacket::encode, PricesPacket::decode, PricesPacket::handle);
    }
}
```
3. 创建需要发送的包
```java
public class PricesPacket {
    private final String itemId;
    private final double price;

    public PricesPacket(String itemId, double price) {
        this.itemId = itemId;
        this.price = price;
    }

    public static PricesPacket decode(FriendlyByteBuf byteBuf) {
        int itemIdLength = byteBuf.readInt(); // 获取 item id length
        byte[] itemIdBytes = new byte[itemIdLength]; // 新建一个 item bytes length 长度的数组用于存item id的数据
        byteBuf.readBytes(itemIdBytes); // 将转换后的item写入byte数组
        return new PricesPacket(new String(itemIdBytes, StandardCharsets.UTF_8), byteBuf.readDouble());
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byte[] bytes = this.itemId.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        byteBuf.writeDouble(price);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                SeasonShop.getLogger().info(this.itemId);
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.displayClientMessage(Component.literal("Item Id: " + this.itemId + " price " + this.price), true);
                }
                // 使用sendToServer 随时发送数据包，使用 reply 在收到包时回复服务器
                NetworkHandler.getNetwork().sendToServer(new PricesPacket(this.itemId, this.price));
                NetworkHandler.getNetwork().reply(new PricesPacket(this.itemId, this.price), ctx);
            }
        });
        ctx.setPacketHandled(true);
    }
}
```
_最后你应该能自定义数据包并且在插件里处理了！恭喜你！！_