<?php

namespace App\Http\Controllers;
use App\User;
use App\Menu;
use App\Cart;
use App\Hpesan;
use App\Dpesan;
use App\Lelang;
use App\Report;
use Illuminate\Support\Facades\Validator;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\Http\Controllers\Controller;

class ming_ctr extends Controller
{
    public function report_specific(Request $reg){
        $report = Report::find($reg->id_laporan);
        return view("admin_reportById",['report' => $report]);
    }
    public function nav_click(Request $reg){
        if($reg->submit == "lelang"){
            $allLelang = DB::table('lelang')->get();
            return view("admin_lelang", ['lelang' => $allLelang]);
        }else if($reg->submit=="merchant"){
            $allMerchant = DB::table('user')->where('role', 'Penjual')->get();
            $allBlock = DB::table('user')->where('role', 'Block')->get();
            return view("admin_merchant", ['merchant' => $allMerchant, 'block' => $allBlock]);
        }else if($reg->submit == "laporan"){
            $allReport = DB::table('report')->orderByRaw('Id_report DESC')->get();
            return view("admin_report",['laporan' => $allReport]);
        }
    }

    public function block_merchant(Request $reg){
        DB::table('user')
            ->where('Username', $reg->Username)
            ->update(['role' => 'Block']);

        $allMerchant = DB::table('user')->where('role', 'Penjual')->get();
        $allBlock = DB::table('user')->where('role', 'Block')->get();
        return view("admin_merchant", ['merchant' => $allMerchant, 'block' => $allBlock]);
    }

    public function unblock_merchant(Request $reg){
        DB::table('user')
            ->where('Username', $reg->Username)
            ->update(['role' => 'Penjual']);

        $allMerchant = DB::table('user')->where('role', 'Penjual')->get();
        $allBlock = DB::table('user')->where('role', 'Block')->get();
        return view("admin_merchant", ['merchant' => $allMerchant, 'block' => $allBlock]);
    }

    public function new_lelang(Request $reg){
        $validator = Validator::make($reg->all(),[
			'judul'=>'required|unique:user,Username',
			'time_start'=>'required',
			'nama'=>'required',
            'bid'=>'required',
            'myfile'=>'required|image'
        ]);

        if($validator->fails()){
			$error = $validator->errors();
            $allLelang = DB::table('lelang')->get();
            return view("admin_lelang", ['lelang' => $allLelang, 'error' => $error]);
		}else{
			Lelang::create([    
                "Nama_lelang" => $reg->judul,
                "Time_end" => $reg->time_start,
                "Barang" => $reg->nama,
                "Current_bid" => $reg->bid,
                "Top_bidder" => "XXXXX"
            ]);
            $allLelang = DB::table('lelang')->get();
            $last_id = DB::table('lelang')->select(DB::raw('max(Id_lelang) as last'))->get();
            //upload
            $filename = $last_id[0]->last.'.jpg';
            $reg->myfile->move(public_path('/images/lelang'), $filename);
            
            return view("admin_lelang", ['lelang' => $allLelang, 'msg' => 'Berhasil Menambahkan lelang']);
		}
    }
}
