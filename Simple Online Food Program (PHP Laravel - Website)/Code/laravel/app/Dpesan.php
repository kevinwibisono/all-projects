<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Dpesan extends Model
{
    protected $table = "Dpesan";
	protected $primaryKey = "Id_detail";
	protected $keyType = "int";
	protected $fillable = ["Id_detail","Nama_menu",'Jumlah_menu', 'Harga_menu', 'Id_pesanan', 'Id_menu'];

	public $timestamps = false;
}
