package com.example.wine.Region;

import com.example.wine.NotFoundException;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
class RegionController {
    private final RegionRepository repository;
    private final RegionModelAssembler assembler;

    RegionController(RegionRepository repository, RegionModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @PostMapping("/region")
    ResponseEntity<?> newRegion(@RequestBody Region newRegion) {

        EntityModel<Region> entityModel = assembler.toModel(repository.save(newRegion));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    @GetMapping("/region")
    CollectionModel<EntityModel<Region>> all() {

        List<EntityModel<Region>> type = repository.findAll().stream() //
                .map(assembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(type, linkTo(methodOn(RegionController.class).all()).withSelfRel());
    }

    @GetMapping("/region/{id}")
    EntityModel<Region> one(@PathVariable Long id) {

        Region region = repository.findById(id) //
                .orElseThrow(() -> new NotFoundException(id));

        return assembler.toModel(region);
    }

    @PutMapping("/region/{id}")
    ResponseEntity<?> replaceRegion(@RequestBody Region newRegion, @PathVariable Long id) {

        Region updatedRegion = repository.findById(id) //
                .map(type -> {
                    type.setName(newRegion.getName());

                    return repository.save(type);
                }) //
                .orElseGet(() -> {
                    newRegion.setId(id);
                    return repository.save(newRegion);
                });

        EntityModel<Region> entityModel = assembler.toModel(updatedRegion);

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    @DeleteMapping("/region/{id}")
    ResponseEntity<?> deleteRegion(@PathVariable Long id) {

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}