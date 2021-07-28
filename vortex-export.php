<?php
/**
 * Run with two arguments, first the backup-file, then the game
 **/
if (count($argv) !== 3) {
	die('Run this script with `php vortex-export.php backup.json skyrimse`');
}
$data = json_decode(file_get_contents($argv[1]));

if (property_exists($data, 'persistent')) {
	if (property_exists($data->persistent, 'mods')) {
		if (property_exists($data->persistent->mods, $argv[2])) {
			foreach (get_object_vars($data->persistent->mods->{$argv[2]}) as $mod => $set) {
				if (property_exists($set, 'attributes') && property_exists($set->attributes, 'modName') && $set->attributes->modName) {
					$mod = $set->attributes->modName;
				}
				if (property_exists($set, 'attributes') && property_exists($set->attributes, 'modId')) {
					echo "{$mod} => https://www.nexusmods.com/skyrimspecialedition/mods/{$set->attributes->modId}\n";
				} elseif (property_exists($set, 'attributes') && property_exists($set->attributes, 'homepage')) {
					echo "{$mod} => {$set->attributes->homepage}\n";
				} else {
					echo "{$mod} =>\n";
				}
			}
		}
	}
}
