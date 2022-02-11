<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\User;
use App\Menu;
use App\Cart;
use App\Hpesan;
use App\Dpesan;
use App\Lelang;
use App\Ticket;
use Illuminate\Support\Facades\Validator;

class main_ctr extends Controller
{
    public function to_login(){
		Cart::truncate();
		return view("login");
	}

	public function to_register(){
		return view("register");
	}

	public function to_detail(Request $reg){
		$reg->session()->put('choosen_merchant', User::find($reg->HiddenMerchant));
		$reg->session()->put('namamenu', '');
		$merchant = User::find($reg->HiddenMerchant);
		$menu = Menu::where('Username_penjual', $merchant->Username)->get();
		return view("detail", ['choosen_merchant' => $merchant, 'menu' => $menu]);
	}

	public function to_topup(Request $reg){
		$singleuser = $reg->session()->get('user_login', null);
		return view("topup", ['current_user' => $singleuser]);
	}

	public function to_home_customer(Request $reg){
        Cart::truncate();
		$allpenjual = User::where('role', 'Penjual')->get();
		$singleuser = $reg->session()->get('user_login', null);
		return view("beranda", ['current_user' => $singleuser, 'penjual' => $allpenjual]);
    }

    public function to_home_merchant(Request $reg){
        $singleuser = $reg->session()->get('user_login', null);
        $allmenu = Menu::where('Username_penjual', $singleuser->Username)->get();
        $allhpesan = Hpesan::where('Username_penjual', $singleuser->Username)->where('Status', 'Menunggu Konfirmasi')->get();
        $alldpesan = Dpesan::all();
		return view("mastermakanan", ['current_user' => $singleuser, 'menu' => $allmenu, 'hpesan' => $allhpesan, 'dpesan' => $alldpesan]);
	}

	public function handle_register(Request $reg){
		$validator = Validator::make($reg->all(),[
			'username'=>'required|unique:user,Username',
			'password'=>'required',
			'nama'=>'required',
            'alamat'=>'required',
            'profileimg' => 'required|image'
		]);

		//update by MICHAEL TENOYO
		if($validator->fails()){
			$error = $validator->errors();
			return view("register", ['error' => $error]);
		}else{
			User::create([
				"Username" => $reg->username,
				"Password" => $reg->password,
				"Nama" => $reg->nama,
				"Alamat" => $reg->alamat,
				"Saldo" => 0,
				"Role" => $reg->role
            ]);
            $reg->profileimg->move(public_path('/images/user'), $reg->username.".jpg");
			return view("login",['error' => "User berhasil dibuat!"]);
        }
	}

	public function handle_login(Request $reg){
		$singleuser = null;
		$role = "";
		$alluser = User::all();
		$allpenjual = User::where('role', 'Penjual')->get();
		foreach($alluser as $p){
			if($p->Username == $reg->username && $p->Password == $reg->password){
				$role = $p->Role;
				$singleuser = User::find($p->Username);
			}
		}
		if($reg->username == "admin" && $reg->password == "admin"){
			return view("header_admin");
		}else if($singleuser == null){
			return view("login", ['error' => "Username atau password yang anda inputkan salah"]);
		}
		else{
			$reg->session()->put('user_login', $singleuser);
			if($role == "Pelanggan"){
				return view("beranda", ['current_user' => $singleuser, 'penjual' => $allpenjual]);
			}
			else if($role == "Penjual"){
				$allmenu = Menu::where('Username_penjual', $singleuser->Username)->get();
                $allhpesan = Hpesan::where('Username_penjual', $singleuser->Username)->where('Status', 'Menunggu Konfirmasi')->get();
                $alldpesan = Dpesan::all();
				return view("mastermakanan", ['current_user' => $singleuser, 'menu' => $allmenu, 'hpesan' => $allhpesan, 'dpesan' => $alldpesan]);
			}else if($role == "Block"){
				return view("home_block",['current_user' => $singleuser]);
			}
		}
	}

	public function handle_topup(Request $reg){
		$saldo = $reg->input_saldo;
		$singleuser = $reg->session()->get('user_login', null);
		User::where("username",$singleuser->Username)->update([
			"Saldo"=>$singleuser->Saldo+$saldo
		]);
		$singleuser = User::find($singleuser->Username);
		$reg->session()->put('user_login', $singleuser);
		return view("topup",['current_user'=>$reg->session()->get('user_login', $singleuser)]);
	}

	public function insert_food(Request $reg){
		$singleuser = $reg->session()->get('user_login', null);

		$validator = Validator::make($reg->all(),[
			'makanan'=>'required',
            'harga'=>'required|numeric',
            'menuimg' => 'required|image'
		]);

		//update by MICHAEL TENOYO
		if($validator->fails()){
			$error = $validator->errors();
            $allmenu = Menu::where('Username_penjual', $singleuser->Username)->get();
            $allhpesan = Hpesan::where('Username_penjual', $singleuser->Username)->where('Status', 'Menunggu Konfirmasi')->get();
			return view("mastermakanan", ['error' => $error,'current_user' => $singleuser, 'menu' => $allmenu]);
        }
        else{
			Menu::create([
				"Nama" => $reg->makanan,
				"Harga" => $reg->harga,
				"Username_penjual" => $singleuser->Username
			]);
			$allmenu = Menu::where('Username_penjual', $singleuser->Username)->get();
			$allhpesan = Hpesan::where('Username_penjual', $singleuser->Username)->where('Status', 'Menunggu Konfirmasi')->get();
            $id_menu = Menu::max('Id_menu');
            $reg->menuimg->move(public_path('/images/menu'), $id_menu.".jpg");
            return view("mastermakanan", ['current_user' => $singleuser, 'menu' => $allmenu, 'hpesan' => $allhpesan]);
		}
	}

	public function add_to_cart(Request $reg){
        $menu = Menu::find($reg->HiddenMenu);
        $jumlah = Cart::where('Nama_menu', $menu->Nama)->get();

        if(count($jumlah) <= 0){
            Cart::create([
                "Id_menu" => $menu->Id_menu,
                "Nama_menu" => $menu->Nama,
                "Harga_menu" => $menu->Harga,
                "Jumlah_menu" => $reg->jumlah,
                "Id_menu" => $reg->HiddenMenu
            ]);
        }
		else{
            foreach($jumlah as $j){
                $jumlahbaru = $j->Jumlah_menu + $reg->jumlah;
                Cart::where('Id_cart', $j->Id_cart)->update([
                    "Jumlah_menu" => $jumlahbaru
                ]);
            }
        }
		$singleuser = $reg->session()->get('user_login', null);
		$merchant = $reg->session()->get('choosen_merchant', null);
		$menumerchant = Menu::where('Nama', 'like', '%'.$reg->session()->get('namamenu', null).'%')->where('Username_penjual', $merchant->Username)->get();
		return view("detail", ['choosen_merchant' => $merchant, 'menu' => $menumerchant]);
	}

	public function checkout(Request $reg){
        $singleuser = $reg->session()->get('user_login', null);
        $allwaiting = Hpesan::where('Username', $singleuser->Username)->where('Status', 'Menunggu Konfirmasi')->get()->count();
		$keranjang = Cart::all();
		return view("checkout", ['current_user' => $singleuser, 'keranjang' => $keranjang, 'jumwaiting' => $allwaiting]);
	}

	public function dec_keranjang(Request $reg){
		$jumlah = Cart::find($reg->HiddenCart)->Jumlah_menu;
		if($jumlah > 0){
			Cart::where('Id_cart', $reg->HiddenCart)->decrement("Jumlah_menu");
		}
        $singleuser = $reg->session()->get('user_login', null);
        $allwaiting = Hpesan::where('Username', $singleuser->Username)->where('Status', 'Menunggu Konfirmasi')->get()->count();
		$keranjang = Cart::all();
		return view("checkout", ['current_user' => $singleuser, 'keranjang' => $keranjang, 'jumwaiting' => $allwaiting]);
	}

	public function inc_keranjang(Request $reg){
		Cart::where('Id_cart', $reg->HiddenCart)->increment("Jumlah_menu");
		$singleuser = $reg->session()->get('user_login', null);
		$allwaiting = Hpesan::where('Username', $singleuser->Username)->where('Status', 'Menunggu Konfirmasi')->get()->count();
		$keranjang = Cart::all();
		return view("checkout", ['current_user' => $singleuser, 'keranjang' => $keranjang, 'jumwaiting' => $allwaiting]);
	}

	public function do_transaction(Request $reg){
		$keranjang = Cart::all();
		$jum = 0;
		foreach ($keranjang as $k) {
			if($k->Jumlah_menu > 0){
				$jum += 1;
			}
		}
		if($jum != 0){
            $singleuser = $reg->session()->get('user_login', null);
            $allwaiting = Hpesan::where('Username', $singleuser->Username)->where('Status', 'Menunggu Konfirmasi')->get()->count();
			if($singleuser->Saldo >= $reg->HiddenTotal && $allwaiting <= 0){
				$merchant = $reg->session()->get('choosen_merchant', null);
				$id = Hpesan::max('Id_pesanan');
				$id += 1;
				Hpesan::create([
					"Id_pesanan" => $id,
					"Tanggal" => date('Y-m-d H:i:s'),
					"Total" => $reg->HiddenTotal,
					"Alamat_pengiriman" => $singleuser->Alamat,
					"Username" => $singleuser->Username,
					"Username_penjual" => $merchant->Username,
					"Status" => "Menunggu Konfirmasi"
				]);
				foreach ($keranjang as $k) {
					if($k->Jumlah_menu > 0){
						Dpesan::create([
                            'Id_menu' => $k->Id_menu,
							"Nama_menu" => $k->Nama_menu,
							"Jumlah_menu" => $k->Jumlah_menu,
							"Harga_menu" => $k->Harga_menu,
							"Id_pesanan" => $id
						]);
					}
				}
            }
            Cart::truncate();
		}
		return view("login");
	}

	public function search_menu(Request $reg){
		$reg->session()->put('namamenu', $reg->NamaMenu);
		$merchant = $reg->session()->get('choosen_merchant', null);
		$menu = Menu::where('Nama', 'like', '%'.$reg->NamaMenu.'%')->where('Username_penjual', $merchant->Username)->get();
		return view("detail", ['choosen_merchant' => $merchant, 'menu' => $menu]);
	}

	public function delete_food(Request $reg){
		$singleuser = $reg->session()->get('user_login', null);
		Menu::where("Id_menu", $reg->HiddenMenu)->delete();
		$allmenu = Menu::where('Username_penjual', $singleuser->Username)->get();
		$allhpesan = Hpesan::where('Username_penjual', $singleuser->Username)->where('Status', 'Menunggu Konfirmasi')->get();
			return view("mastermakanan", ['current_user' => $singleuser, 'menu' => $allmenu, 'hpesan' => $allhpesan]);
	}

	public function edit_food(Request $reg){
		$singleuser = $reg->session()->get('user_login', null);
		Menu::where('Id_menu', $reg->hidden_id)->update([
			"Nama" => $reg->nama_menu,
			"Harga" => $reg->new_harga
		]);
		$allmenu = Menu::where('Username_penjual', $singleuser->Username)->get();
		$allhpesan = Hpesan::where('Username_penjual', $singleuser->Username)->where('Status', 'Menunggu Konfirmasi')->get();
		return view("mastermakanan", ['current_user' => $singleuser, 'menu' => $allmenu, 'hpesan' => $allhpesan]);
	}

	public function to_lelang(Request $reg){
		$singleuser = $reg->session()->get('user_login', null);
		$lelang = Lelang::all();
		return view("lelang", ['current_user' => $singleuser, 'lelang' => $lelang]);
    }

    public function accept_order(Request $reg){
        $hpesan = Hpesan::find($reg->HiddenPesan);

        Hpesan::where('Id_pesanan', $reg->HiddenPesan)->update([
			"Status" => "Diterima"
        ]);

        $newsaldo = User::find($hpesan->Username)->Saldo - $hpesan->Total;

        User::where('Username', $hpesan->Username)->update([
			"Saldo" => $newsaldo
        ]);

        $newsaldopenjual = User::find($hpesan->Username_penjual)->Saldo + $hpesan->Total;

        User::where('Username', $hpesan->Username_penjual)->update([
			"Saldo" => $newsaldopenjual
        ]);

        $singleuser = $reg->session()->get('user_login', null);
        $allmenu = Menu::where('Username_penjual', $singleuser->Username)->get();
		$allhpesan = Hpesan::where('Username_penjual', $singleuser->Username)->where('Status', 'Menunggu Konfirmasi')->get();
		return view("mastermakanan", ['current_user' => $singleuser, 'menu' => $allmenu, 'hpesan' => $allhpesan]);
    }
}
