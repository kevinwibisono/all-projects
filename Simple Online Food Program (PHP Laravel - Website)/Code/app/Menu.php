<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Menu extends Model
{
    protected $table = "Menu";
	protected $primaryKey = "Id_menu";
	protected $keyType = "int";
	protected $fillable = ["Nama",'Harga', 'Username_penjual'];

	public $timestamps = false;
}
