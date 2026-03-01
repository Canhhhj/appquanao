# Script commit nhieu lan - tieng Viet (khong dau de tranh loi encoding)
$root = "D:\appquanaoagile"
Set-Location $root

# 1. Models
git add app/app/src/main/java/com/example/myapplication/model/Address.java
git add app/app/src/main/java/com/example/myapplication/model/Notification.java
git add app/app/src/main/java/com/example/myapplication/model/Review.java
git commit -m "App: Them model Dia chi, Thong bao, Danh gia"

# 2. Adapter - Review, Notification, Address
git add app/app/src/main/java/com/example/myapplication/adapter/ReviewAdapter.java
git add app/app/src/main/java/com/example/myapplication/adapter/NotificationAdapter.java
git add app/app/src/main/java/com/example/myapplication/adapter/AddressAdapter.java
git commit -m "App: Them adapter Danh gia, Thong bao, Dia chi"

# 3. Tien ich yeu thich
git add app/app/src/main/java/com/example/myapplication/utils/FavoritesManager.java
git commit -m "App: Them FavoritesManager luu san pham yeu thich"

# 4. Man hinh So dia chi
git add app/app/src/main/java/com/example/myapplication/AddressActivity.java
git add app/app/src/main/res/layout/activity_addresses.xml
git add app/app/src/main/res/layout/item_address.xml
git commit -m "App: Man hinh So dia chi - chon dia chi giao hang"

# 5. Man hinh Thong bao
git add app/app/src/main/java/com/example/myapplication/NotificationsActivity.java
git add app/app/src/main/res/layout/activity_notifications.xml
git add app/app/src/main/res/layout/item_notification.xml
git commit -m "App: Man hinh Thong bao"

# 6. Man hinh Lien he shop
git add app/app/src/main/java/com/example/myapplication/ContactActivity.java
git add app/app/src/main/res/layout/activity_contact.xml
git commit -m "App: Man hinh Lien he shop - goi dien, email"

# 7. Man hinh Yeu thich
git add app/app/src/main/java/com/example/myapplication/FavoritesActivity.java
git add app/app/src/main/res/layout/activity_favorites.xml
git commit -m "App: Man hinh San pham yeu thich"

# 8. Chi tiet don hang + theo doi van chuyen
git add app/app/src/main/java/com/example/myapplication/OrderDetailActivity.java
git add app/app/src/main/res/layout/activity_order_detail.xml
git add app/app/src/main/res/layout/item_order_detail_status.xml
git add app/app/src/main/res/layout/item_order_product.xml
git commit -m "App: Chi tiet don hang va theo doi trang thai van chuyen"

# 9. Tim kiem theo API + ProductList
git add app/app/src/main/java/com/example/myapplication/ProductListActivity.java
git add app/app/src/main/res/layout/activity_product_list.xml
git commit -m "App: Tim kiem san pham theo API va man hinh ket qua"

# 10. Layout + drawable gio hang, tai khoan
git add app/app/src/main/res/drawable/ic_cart_empty.xml
git add app/app/src/main/res/drawable/bg_bottom_bar_cart.xml
git add app/app/src/main/res/drawable/bg_btn_quantity.xml
git add app/app/src/main/res/drawable/bg_btn_quantity_primary.xml
git add app/app/src/main/res/drawable/circle_dot.xml
git add app/app/src/main/res/drawable/circle_dot_done.xml
git commit -m "App: Drawable cho gio hang va trang thai don hang"

# 11. Drawable tai khoan
git add app/app/src/main/res/drawable/avatar_circle.xml
git add app/app/src/main/res/drawable/ic_person_profile.xml
git add app/app/src/main/res/drawable/bg_profile_header.xml
git add app/app/src/main/res/drawable/bg_menu_item.xml
git add app/app/src/main/res/drawable/bg_btn_logout.xml
git add app/app/src/main/res/drawable/bg_btn_login_profile.xml
git commit -m "App: Drawable cho man hinh Tai khoan"

# 12. Con lai drawable
git add app/app/src/main/res/drawable/bg_card_rounded.xml
git add app/app/src/main/res/drawable/bg_category_circle.xml
git add app/app/src/main/res/drawable/bg_category_selected.xml
git add app/app/src/main/res/drawable/bg_voucher_accent.xml
git commit -m "App: Cap nhat drawable chung"

# 13. Layout item review, category, product
git add app/app/src/main/res/layout/item_review.xml
git add app/app/src/main/res/layout/item_category.xml
git add app/app/src/main/res/layout/item_category_mall.xml
git add app/app/src/main/res/layout/item_product.xml
git add app/app/src/main/res/layout/item_live_voucher.xml
git commit -m "App: Layout item danh gia, danh muc, san pham"

# 14. Layout activity + fragment con lai
git add app/app/src/main/res/layout/activity_login.xml
git add app/app/src/main/res/layout/activity_main.xml
git add app/app/src/main/res/layout/activity_register.xml
git add app/app/src/main/res/layout/activity_splash.xml
git add app/app/src/main/res/layout/fragment_mall.xml
git add app/app/src/main/res/menu/bottom_nav_menu.xml
git commit -m "App: Layout dang nhap, main, dang ky, splash, mall, menu"

# 15. Values
git add app/app/src/main/res/values/colors.xml
git add app/app/src/main/res/values/strings.xml
git add app/app/src/main/res/values/themes.xml
git commit -m "App: Mau sac, chuoi, theme"

# 16. Adapter Category, Mall
git add app/app/src/main/java/com/example/myapplication/adapter/CategoryAdapter.java
git add app/app/src/main/java/com/example/myapplication/adapter/MallCategoryAdapter.java
git commit -m "App: Cap nhat adapter Danh muc"

Write-Host "Xong. Chay git push de day len."
Set-Location $root; git status -s
