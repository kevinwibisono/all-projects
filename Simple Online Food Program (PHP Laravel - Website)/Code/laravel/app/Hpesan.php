<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Hpesan extends Model
{
    protected $table = "Hpesan";
	protected $primaryKey = "Id_pesanan";
	protected $keyType = "int";
	protected $fillable = ["Id_pesanan","Tanggal",'Total', 'Alamat_pengiriman', 'Username', 'Username_penjual', 'Status'];

	public $timestamps = false;

	public function Pembeli(){
		return $this->belongsTo("App\User", "Username", "Username");
	}

	public function Penjual()
	{
		return $this->belongsTo('App\User', 'Username_penjual', 'Username');
	}
}
