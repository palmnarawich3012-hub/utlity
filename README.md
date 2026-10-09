# Police Utility (Fabric 1.20.1)

ไอเทม: ไฟฉาย, กุญแจมือ, เทเซอร์, วิทยุ, เข็มขัดเก็บของ (แท็บครีเอทีฟ "อุปกรณ์ตำรวจ")

## วิธีบิลด์
1. ติดตั้ง JDK 17 และ Gradle 8.6+  (หรือเปิดโฟลเดอร์นี้ด้วย IntelliJ IDEA แล้วให้มัน import Gradle)
2. รันในโฟลเดอร์โปรเจกต์:  `gradle wrapper --gradle-version 8.6`  แล้ว `./gradlew build`
3. ไฟล์มอดอยู่ที่ `build/libs/police-utility-1.0.0.jar` -> ใส่ในโฟลเดอร์ mods (ต้องมี Fabric API 0.92.2+1.20.1)

## วิธีใช้
- ไฟฉาย: คลิกขวาเปิด/ปิด ถือแล้วจะมีแสงตามจุดที่มอง (ใช้บล็อก Light ชั่วคราว)
- กุญแจมือ: คลิกขวาที่ผู้เล่น/มอนสเตอร์เพื่อใส่ ใช้ซ้ำเพื่อปลด (เดินแทบไม่ได้ ตี/วางบล็อก/ใช้ไอเทมไม่ได้)
- เทเซอร์: คลิกขวายิงช็อต ระยะ 8 บล็อก คูลดาวน์ 3 วิ ทนทาน 64 ซ่อมด้วย Redstone ที่ทั่ง
- วิทยุ: คลิกขวาเปลี่ยนช่อง 1-9, Shift+คลิกขวาเปิด/ปิด ถือวิทยุที่เปิดแล้วพิมพ์แชท = พูดเข้าช่องวิทยุ
- เข็มขัด: คลิกขวาเปิดช่องเก็บของ 18 ช่อง

## ปรับโมเดล 3D
โมเดลอยู่ที่ assets/policeutility/models/item/*.json และ texture ที่ textures/item/*.png (เปิดแก้ใน Blockbench ได้)

## โมเดล 3D แบบเมช (โค้งมน) ทั้ง 5 ชิ้น
- ไฟล์ OBJ: `assets/policeutility/models/obj/{taser,flashlight,handcuffs,radio,utility_belt}.obj` + texture `textures/item/*_obj.png`
- โหลดด้วยโค้ดฝั่งไคลเอนต์ `client/PoliceUtilityClient.java` + `ObjItemModel.java` (Fabric Model Loading API + Renderer API)
- ถ้าโหลดไม่ได้ เกมจะใช้โมเดลกล่องเหลี่ยมเดิม (`models/item/*.json`) แทน
- ถ้าคอมไพล์ไม่ผ่านเพราะชื่อ API ต่างเวอร์ชัน ให้ลบโฟลเดอร์ `client/` กับบรรทัด "client" ใน fabric.mod.json จะกลับไปใช้โมเดลกล่องเหลี่ยม

## เทเซอร์
ยิงโดนแล้วเป้าหมายติด: ช้า + อ่อนแรง + มึน + **พิษ 8 วินาที** (พิษใน Minecraft ไม่ฆ่าเป้าหมายจนตาย เหลือเลือดอย่างน้อย 1)

## ท่าถือ (display) และการปรับแต่ง
- ค่าตำแหน่ง/มุมตอนถือของเทเซอร์และไฟฉายอยู่ใน `models/item/taser.json` และ `flashlight.json` ช่อง `display`
  (`firstperson_righthand` = มุมมองตัวเอง, `thirdperson_righthand` = มุมมองคนอื่น)
- ถ้าถือแล้วเอียง/ลอย/ใหญ่ไป ให้ปรับ `rotation`, `translation`, `scale` ตรงนั้น (เปิดดูสดใน Blockbench ได้) แล้วกด F3+T ในเกมเพื่อโหลดรีซอร์สใหม่
- เทเซอร์: ตอนถือจะมีจุดเลเซอร์สีแดงตรงจุดที่เล็ง (โค้ด `TaserLaser.java`) และหายไปตอนอยู่ในคูลดาวน์
