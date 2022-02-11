<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Report extends Model
{
  protected $table = "report";
	protected $primaryKey = "Id_report";
	protected $keyType = "int";
	protected $fillable = ["Id_report", "Id_merchant", "Message", "Foto_bukti", "Reported_by"];
	public $timestamps = false;
}
