<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\User;
use App\Menu;
use App\Cart;
use App\Hpesan;
use App\Dpesan;
use App\Report;
use Illuminate\Support\Facades\Validator;

class mitchell_ctr extends Controller
{
    public function to_profile(){
      $user = session()->get('user_login', null);
      $hpesan = DB::table('Hpesan')->where('Username', $user->Username)->get();
      $hpesan = json_decode($hpesan, true);
      $dpesanAll = Dpesan::all();
      for ($i=0; $i < count($hpesan); $i++) {
        $hpesan[$i]['dpesan'] = "";
        for ($j=0; $j < count($dpesanAll); $j++) {
          if ($hpesan[$i]['Id_pesanan'] == $dpesanAll[$j]->Id_pesanan) {
            $hpesan[$i]['dpesan'] = $hpesan[$i]['dpesan'] . ";nama:" . $dpesanAll[$j]->Nama_menu . ";jumlah:" . $dpesanAll[$j]->Jumlah_menu . ";harga:" . $dpesanAll[$j]->Harga_menu . "\n";
          }
        }
      }
      return view("profile", ['user' => $user, 'hpesan' => $hpesan]);
    }

    public function to_report_merchant(Request $req){
        $user = $req->session()->get('user_login', null);
  		$merchant = User::find($req->HiddenMerchant);
      return view("report_merchant", ['user' => $user, 'merchant' => $merchant]);
    }

    public function report_merchant(Request $req){
      $user = $req->session()->get('user_login', null);
      $merchant = User::find($req->HiddenMerchant);

      $allreport = Report::all();
      if (count($allreport) > 0) {
        $max = $allreport[count($allreport) - 1]->Id_report + 1;
      }else{
        $max = 1;
      }

      $validator = Validator::make($req->all(),[
  			'message'=>'required',
            'proof' => 'required|image'
  	    ]);

  		if($validator->fails()){
  			$error = $validator->errors();
            return view("report_merchant", ['merchant' => $merchant, 'error' => $error]);
  		}else{
        $nama_foto = date("Y-m-d") . "-" . $user->Username . "-" . $merchant->Username;
  			Report::create([
          "Id_report" => $max,
          "Id_merchant" => $merchant->Username,
  				"Message" => $req->message,
  				"Foto_bukti" => $nama_foto,
  				"Reported_by" => $user->Username
        ]);
        $req->proof->move(public_path('/images/report'), $nama_foto . ".jpg");
    		$allpenjual = User::where('role', 'Penjual')->get();
        return view("beranda", ['current_user' => $user, 'penjual' => $allpenjual]);
      }
    }
}
