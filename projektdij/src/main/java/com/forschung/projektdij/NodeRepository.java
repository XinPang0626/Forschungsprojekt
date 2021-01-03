package com.forschung.projektdij;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NodeRepository extends CrudRepository<Nodes,Long>{
    
}
