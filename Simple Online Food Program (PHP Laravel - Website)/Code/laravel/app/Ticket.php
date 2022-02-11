<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Ticket extends Model
{
    protected $table = "Ticket";
	protected $primaryKey = "Id_tiket";
	protected $keyType = "int";
	protected $fillable = ["Id_lelang",'Username'];

	public $timestamps = false;
}
