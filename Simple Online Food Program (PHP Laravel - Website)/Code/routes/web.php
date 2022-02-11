<?php

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', 'main_ctr@to_login');
Route::post('/', 'main_ctr@to_login');
Route::post('/registrasi', 'main_ctr@to_register');
Route::post('/handle_register', 'main_ctr@handle_register');
Route::post('/handle_login', 'main_ctr@handle_login');
Route::post('/handle_bid','mike_ctr@handle_bid');
Route::post('/to_detail', 'main_ctr@to_detail');
Route::post('/to_topup', 'main_ctr@to_topup');
Route::post('/to_home_customer', 'main_ctr@to_home_customer');
Route::post('/to_home_merchant', 'main_ctr@to_home_merchant');
Route::post('/handle_insert_food', 'main_ctr@insert_food');
Route::post('/handle_topup', 'main_ctr@handle_topup');
Route::post('/add_to_cart', 'main_ctr@add_to_cart');
Route::post('/checkout', 'main_ctr@checkout');
Route::post('/dec_keranjang', 'main_ctr@dec_keranjang');
Route::post('/inc_keranjang', 'main_ctr@inc_keranjang');
Route::post('/do_transaction', 'main_ctr@do_transaction');
Route::post('/search_menu', 'main_ctr@search_menu');
Route::post('/delete_food', 'main_ctr@delete_food');
Route::post('/edit_food', 'main_ctr@edit_food');
Route::post('/profile', 'mitchell_ctr@to_profile');
Route::post('/nav_admin','ming_ctr@nav_click');
Route::post('/report_specific','ming_ctr@report_specific');
Route::post('/nav_lelang', 'main_ctr@to_lelang');
Route::post('/enter_lelang', 'mike_ctr@enter_lelang');
Route::post('/insert_lelang','ming_ctr@new_lelang');
Route::post('/accept_order','main_ctr@accept_order');
Route::post('/block_merchant','ming_ctr@block_merchant');
Route::post('/unblock_merchant','ming_ctr@unblock_merchant');

Route::post('/to_report_merchant', 'mitchell_ctr@to_report_merchant');
Route::post('/report_merchant', 'mitchell_ctr@report_merchant');
Route::get('/insert_bid','mike_ctr@insert_bid');
Route::get('/get_bid_tertinggi','mike_ctr@get_bid_tertinggi');
Route::get('/get_count_down','mike_ctr@get_count_down');
