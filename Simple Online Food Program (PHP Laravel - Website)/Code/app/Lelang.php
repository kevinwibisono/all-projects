<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Lelang extends Model
{
    protected $table = "Lelang"; 
	protected $primaryKey = "Id_lelang";  
	protected $keyType = "int";  
	protected $fillable = ["Nama_lelang","Time_end",'Barang', 'Current_bid', 'Top_bidder'];

	public $timestamps = false;
}
