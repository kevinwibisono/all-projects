<?php
	
class Database extends CI_Model{
	public function __construct(){
		parent::__construct();
		$this->load->database();
	}
	
	public function insertMhs($nama, $alamat, $telepon){
		$this->db->query("INSERT INTO Mhs values(NULL, '$nama', '$alamat', '$telepon')");
	}
	
	public function updateMhs($id, $nama, $alamat, $telepon){
		$this->db->query("UPDATE Mhs SET nama = '$nama', alamat = '$alamat', telepon = '$telepon' where id= $id");
		return $this->db->affected_rows();
	}
	
	public function getallmhs(){
		return $this->db->query("SELECT * FROM Mhs ORDER BY id")->result();
	}
	
	public function getDetail($id){
		return $this->db->query("SELECT * FROM Mhs  WHERE id= $id")->row();
	}
}

?>