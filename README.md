# Auto Enchant (Fabric 1.21.11)

## Chức năng
- Nhấn **Shift phải** (đổi được trong `Options > Controls > Key Binds > Auto Enchant`)
  để mở một GUI nhỏ chỉ có **1 nút** bật/tắt tính năng.
- Có sẵn thêm một phím tắt riêng (`key.autoenchant.toggle`, mặc định chưa gán)
  để bật/tắt nhanh mà không cần mở GUI — vào Controls để tự gán phím bạn muốn.
- Khi tính năng đang **BẬT**: mỗi tick client sẽ kiểm tra level kinh nghiệm
  của bạn. Khi level ≥ 60:
  1. Đợi 1.1 giây.
  2. Vung tay (giả lập click chuột trái) rồi gửi lệnh `/enchant`.
  3. Đợi GUI mở, bấm vào **slot 13**, rồi tự đóng GUI lại.

## Cách build
1. Cài JDK 21 và cấu trúc project Fabric Loom chuẩn (đã có sẵn `build.gradle`,
   `settings.gradle`, `gradle.properties` trong thư mục này).
2. Kiểm tra lại trên https://fabricmc.net/develop để xác nhận đúng phiên bản
   `yarn_mappings`, `loader_version`, `fabric_version` tương ứng với Minecraft
   1.21.11 tại thời điểm bạn build — các giá trị trong `gradle.properties`
   chỉ là ví dụ và có thể đã lỗi thời.
3. Chạy `./gradlew build` (hoặc `gradlew.bat build` trên Windows) — cần
   Gradle Wrapper, có thể tạo bằng `gradle wrapper` nếu chưa có.
4. File `.jar` build ra nằm trong `build/libs/`, bỏ vào thư mục `mods` của
   Fabric Loader (Fabric API cũng phải được cài).

## Lưu ý quan trọng
- Tên lệnh `/enchant` và số **slot 13** được viết theo đúng mô tả bạn đưa ra.
  Nếu server bạn dùng có cú pháp lệnh hoặc bố cục GUI khác, hãy chỉnh lại
  hằng số `TARGET_SLOT` và chuỗi lệnh trong `AutoEnchantMod.java` cho khớp.
- Tên method/class dùng mapping Yarn có thể thay đổi giữa các bản snapshot
  1.21.11 — nếu build lỗi do đổi tên method, đối chiếu lại theo mapping mới
  nhất qua Linkie hoặc trang fabricmc.net/develop.
- **Nhiều server Minecraft cấm hành vi tự động hoá thao tác (autoclicker/macro)
  và có thể coi đây là gian lận, dẫn đến khoá tài khoản.** Hãy kiểm tra quy
  định của server bạn chơi trước khi dùng, mình chỉ cung cấp phần code theo
  đúng yêu cầu kỹ thuật.

