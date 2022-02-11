<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class User extends Model
{
    protected $table = "User"; 
	protected $primaryKey = "Username";  
	protected $keyType = "string";  
	protected $fillable = ["Username","Password",'Nama', 'Alamat', 'Saldo', 'Role'];

	public $timestamps = false;
}
