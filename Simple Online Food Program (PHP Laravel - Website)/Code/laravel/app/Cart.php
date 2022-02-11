<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Cart extends Model
{
    protected $table = "Cart";
	protected $primaryKey = "Id_cart";
	protected $keyType = "int";
	protected $fillable = ["Nama_menu",'Jumlah_menu', 'Harga_menu', 'Id_menu'];

	public $timestamps = false;
}
