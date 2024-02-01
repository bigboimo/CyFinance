package coms309.items;

import coms309.people.Person;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author James Joseph
 */
@RestController
public class ItemController {

    HashMap<String, Item> itemList = new  HashMap<>();

    @GetMapping("/items")
    public HashMap<String, Item> getItemList(){return itemList;}

    @GetMapping("/items/{name}")
    public Item getItem(@PathVariable String name){return itemList.get(name);}

    @PostMapping("/items")
    public String makeItem(Item item){
        System.out.println(item);
        itemList.put(item.getName(), item);
        return "New Item:\n" + item.toString();
    }

    @PutMapping("/items")
    public String updateItem(Item item){
        itemList.replace(item.getName(), item);
        return itemList.get(item.getName()).toString();
    }

    @DeleteMapping("/items/{name}")
    public String removeItem(Item item){
        itemList.remove(item.getName());
        return "Removed: " + item.getName();
    }
}
