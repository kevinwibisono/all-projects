<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\Http\Requests;
use App\Http\Controllers\Controller;
use App\User;
use App\Menu;
use App\Cart;
use App\Hpesan;
use App\Dpesan;
use App\Lelang;
use App\Ticket;
use Carbon\Carbon;
use DateTime;
use Illuminate\Support\Facades\Validator;

class mike_ctr extends Controller
{
    public function enter_lelang(Request $reg ){
        $username = $reg->session()->get('user_login',null);
        $user_sudah_ada = Ticket::where("Id_lelang",$reg->HiddenLelang)->where("Username", $username->Username)->get()->count();
        $jumlah_ticket = Ticket::where("Username", $username->Username)->get()->count();
        if($user_sudah_ada >0){
            $lelang = Lelang::find($reg->HiddenLelang);
            $reg->session()->put('current_lelang', $lelang);
            return view("dalam_lelang",['current_user' =>$username,'lelang' => $lelang]);
        }
        else{
            if($jumlah_ticket > 3){
                echo "<script>";
                echo "alert('Anda memiliki terlalu banyak tiket');";
                echo "</script>";
                $lelang = Lelang::all();
                return view("lelang", ['current_user' => $username, 'lelang' => $lelang]);
            }
            else{
                Ticket::create([
                    "Id_lelang" => $reg->HiddenLelang,
                    "Username" => $username->Username
                ]);
                $lelang = Lelang::find($reg->HiddenLelang);
                $reg->session()->put('current_lelang', $lelang);
                return view("dalam_lelang",['current_user' =>$username,'lelang' => $lelang]);
            }
        }
        echo $jumlah_ticket;
    }

    public function handle_bid(Request $reg){
        $username = $reg->session()->get('user_login',null);
    }

    public function get_bid_tertinggi(Request $reg){
        $username = $reg->session()->get('user_login',null);
        $lelang = $reg->session()->get('current_lelang',null);

        $lelang = Lelang::find($lelang->Id_lelang);
        $reg->session()->put('current_lelang', $lelang);

        $msg = $lelang->Current_bid;

        return response()->json(array('msg'=> $msg), 200);
    }

    public function get_count_down(Request $reg){
        $lelang = $reg->session()->get('current_lelang',null);
        $username = $reg->session()->get('user_login',null);

        $mytime = Carbon::now();    
        $endtime = Carbon::parse($lelang->Time_end);

        $result = $mytime->diffInSeconds($endtime, false);

        $result = gmdate('H:i:s',$result);

        return response()->json(array('count_down' => $result), 200);
    }

    public function insert_bid(Request $reg){
        $lelang = $reg->session()->get('current_lelang',null);
        $Id_user = $reg->session()->get('user_login',null);
        $msg = "Bid terlalu sedikit";
        if($lelang->Current_bid > $reg->bid){
        }
        else{
            $msg = "Bid berhasil";
            Lelang::where('Id_lelang',$lelang->Id_lelang)->update(["Current_bid" => $reg->bid]);
            Lelang::where('Id_lelang',$lelang->Id_lelang)->update(["Top_bidder" => $Id_user->Username]);
        }
        return response()->json(array('pesan_insert'=> $msg), 200);
    }
}
