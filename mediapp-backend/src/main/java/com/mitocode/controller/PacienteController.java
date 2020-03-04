package com.mitocode.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mitocode.exception.ModeloNotFoundException;
import com.mitocode.model.Paciente;
import com.mitocode.service.IPacienteService;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

	@Autowired
	private IPacienteService service;
	
	@GetMapping(produces = "application/json")
	public ResponseEntity<List<Paciente>> listar(){
		return new ResponseEntity<List<Paciente>>(service.listar(),HttpStatus.OK);
		//se debe poder manipular el status code de la petición
	}
	
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Resource<Paciente> listarPorId(@PathVariable("id") Integer id) {
		Paciente pac = service.listarId(id);
		if(pac == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO: " + id);
		}
		
		Resource<Paciente> resource = new Resource<Paciente>(pac);
	
		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).listarPorId(id));
		resource.add(linkTo.withRel("paciente-resource"));
		
		return resource;
	}
	
	
	//para que transforme el json en un objeto de tipo Paciente agregamos la anotacion 
	//@RequestBody
	@PostMapping(produces = "application/jason", consumes = "application/jason")
	public ResponseEntity<Object> registrar(@RequestBody Paciente pac) {
		Paciente paciente = new Paciente();
		paciente = service.registrar(pac);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(paciente.getIdpaciente()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PutMapping(produces = "application/jason", consumes = "application/jason") 
	public ResponseEntity<Object> modificar(@RequestBody Paciente pac) {
		service.modificar(pac);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
	/*
	@DeleteMapping(value = "/{id}")
	public void eliminar(@PathVariable("id") Integer id){
		service.eliminar(id);
	}
	*/
	
	@DeleteMapping(value = "/{id}")
	public void elminar(@PathVariable("id") Integer id) {
		Paciente pac = service.listarId(id);
		if (pac == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO: " + id);
		} else {
			service.eliminar(id);
		}
	}
	
}
