// Public JS API for @babashka/fs, compiled from src/babashka/fs.cljc by squint.
// The squint-compiled names (exists_QMARK_, copy_tree, ...) are re-exported for
// direct interop; the friendly camelCase names are the documented JS surface.
import {
  create_temp_dir,
  last_modified_time,
  xdg_config_home,
  owner,
  file_time__GT_millis,
  canonicalize,
  posix_file_permissions,
  regular_file_QMARK_,
  normalize,
  which,
  which_all,
  get_attribute,
  copy,
  creation_time,
  exists_QMARK_,
  create_file,
  directory_QMARK_,
  starts_with_QMARK_,
  file_separator,
  set_creation_time,
  absolutize,
  parent,
  create_dirs,
  path,
  delete$,
  create_temp_file,
  set_attribute,
  gzip,
  strip_ext,
  set_posix_file_permissions,
  update_file,
  root,
  split_ext,
  xdg_cache_home,
  touch,
  temp_dir,
  cwd,
  millis__GT_file_time,
  real_path,
  path_separator,
  delete_on_exit,
  extension,
  write_lines,
  file_time__GT_instant,
  list_dirs,
  read_all_lines,
  create_sym_link,
  readable_QMARK_,
  posix__GT_str,
  delete_tree,
  str__GT_posix,
  writable_QMARK_,
  create_dir,
  read_attributes_STAR_,
  read_all_bytes,
  list_dir,
  absolute_QMARK_,
  ends_with_QMARK_,
  hidden_QMARK_,
  gunzip,
  expand_home,
  move,
  home,
  glob__GT_regex,
  match,
  write_bytes,
  unzip,
  delete_if_exists,
  instant__GT_file_time,
  xdg_state_home,
  file,
  components,
  size,
  modified_since,
  same_file_QMARK_,
  exec_paths,
  split_paths,
  windows_QMARK_,
  xdg_data_home,
  relativize,
  copy_tree,
  glob,
  set_last_modified_time,
  walk_file_tree,
  relative_QMARK_,
  create_link,
  zip,
  read_link,
  executable_QMARK_,
  sym_link_QMARK_,
  unixify,
  read_attributes,
  file_name,
} from './lib/babashka/fs.mjs';

// camelCase (or dashed) option keys are translated to the dashed keyword
// strings the squint-compiled functions read, e.g. {posixFilePermissions}
// and {visitFile} become {'posix-file-permissions'} / {'visit-file'}.
const kebabKey = (k) => k.replace(/[A-Z]/g, (m) => '-' + m.toLowerCase());
const isPlainObject = (x) =>
  x != null && typeof x === 'object' && Object.getPrototypeOf(x) === Object.prototype;
const kebabizeArg = (x) => {
  if (!isPlainObject(x)) return x;
  const out = {};
  for (const k of Object.keys(x)) out[kebabKey(k)] = x[k];
  return out;
};
const jsFriendly = (fn) => (...args) => fn(...args.map(kebabizeArg));

const createTempDir = jsFriendly(create_temp_dir);
const lastModifiedTime = jsFriendly(last_modified_time);
const xdgConfigHome = jsFriendly(xdg_config_home);
const fileTimeToMillis = jsFriendly(file_time__GT_millis);
const posixFilePermissions = jsFriendly(posix_file_permissions);
const isRegularFile = jsFriendly(regular_file_QMARK_);
const whichAll = jsFriendly(which_all);
const getAttribute = jsFriendly(get_attribute);
const creationTime = jsFriendly(creation_time);
const exists = jsFriendly(exists_QMARK_);
const createFile = jsFriendly(create_file);
const isDirectory = jsFriendly(directory_QMARK_);
const startsWith = jsFriendly(starts_with_QMARK_);
const fileSeparator = file_separator;
const setCreationTime = jsFriendly(set_creation_time);
const createDirs = jsFriendly(create_dirs);
const createTempFile = jsFriendly(create_temp_file);
const setAttribute = jsFriendly(set_attribute);
const stripExt = jsFriendly(strip_ext);
const setPosixFilePermissions = jsFriendly(set_posix_file_permissions);
const updateFile = jsFriendly(update_file);
const splitExt = jsFriendly(split_ext);
const xdgCacheHome = jsFriendly(xdg_cache_home);
const tempDir = jsFriendly(temp_dir);
const millisToFileTime = jsFriendly(millis__GT_file_time);
const realPath = jsFriendly(real_path);
const pathSeparator = path_separator;
const deleteOnExit = jsFriendly(delete_on_exit);
const writeLines = jsFriendly(write_lines);
const fileTimeToInstant = jsFriendly(file_time__GT_instant);
const listDirs = jsFriendly(list_dirs);
const readAllLines = jsFriendly(read_all_lines);
const createSymLink = jsFriendly(create_sym_link);
const isReadable = jsFriendly(readable_QMARK_);
const posixToStr = jsFriendly(posix__GT_str);
const deleteTree = jsFriendly(delete_tree);
const strToPosix = jsFriendly(str__GT_posix);
const isWritable = jsFriendly(writable_QMARK_);
const createDir = jsFriendly(create_dir);
const readAttributesRaw = jsFriendly(read_attributes_STAR_);
const readAllBytes = jsFriendly(read_all_bytes);
const listDir = jsFriendly(list_dir);
const isAbsolute = jsFriendly(absolute_QMARK_);
const endsWith = jsFriendly(ends_with_QMARK_);
const isHidden = jsFriendly(hidden_QMARK_);
const expandHome = jsFriendly(expand_home);
const globToRegex = jsFriendly(glob__GT_regex);
const writeBytes = jsFriendly(write_bytes);
const deleteIfExists = jsFriendly(delete_if_exists);
const instantToFileTime = jsFriendly(instant__GT_file_time);
const xdgStateHome = jsFriendly(xdg_state_home);
const modifiedSince = jsFriendly(modified_since);
const isSameFile = jsFriendly(same_file_QMARK_);
const execPaths = jsFriendly(exec_paths);
const splitPaths = jsFriendly(split_paths);
const isWindows = jsFriendly(windows_QMARK_);
const xdgDataHome = jsFriendly(xdg_data_home);
const copyTree = jsFriendly(copy_tree);
const setLastModifiedTime = jsFriendly(set_last_modified_time);
const walkFileTree = jsFriendly(walk_file_tree);
const isRelative = jsFriendly(relative_QMARK_);
const createLink = jsFriendly(create_link);
const readLink = jsFriendly(read_link);
const isExecutable = jsFriendly(executable_QMARK_);
const isSymlink = jsFriendly(sym_link_QMARK_);
const readAttributes = jsFriendly(read_attributes);
const fileName = jsFriendly(file_name);

export {
  // squint names
  create_temp_dir,
  last_modified_time,
  xdg_config_home,
  owner,
  file_time__GT_millis,
  canonicalize,
  posix_file_permissions,
  regular_file_QMARK_,
  normalize,
  which,
  which_all,
  get_attribute,
  copy,
  creation_time,
  exists_QMARK_,
  create_file,
  directory_QMARK_,
  starts_with_QMARK_,
  file_separator,
  set_creation_time,
  absolutize,
  parent,
  create_dirs,
  path,
  delete$,
  create_temp_file,
  set_attribute,
  gzip,
  strip_ext,
  set_posix_file_permissions,
  update_file,
  root,
  split_ext,
  xdg_cache_home,
  touch,
  temp_dir,
  cwd,
  millis__GT_file_time,
  real_path,
  path_separator,
  delete_on_exit,
  extension,
  write_lines,
  file_time__GT_instant,
  list_dirs,
  read_all_lines,
  create_sym_link,
  readable_QMARK_,
  posix__GT_str,
  delete_tree,
  str__GT_posix,
  writable_QMARK_,
  create_dir,
  read_attributes_STAR_,
  read_all_bytes,
  list_dir,
  absolute_QMARK_,
  ends_with_QMARK_,
  hidden_QMARK_,
  gunzip,
  expand_home,
  move,
  home,
  glob__GT_regex,
  match,
  write_bytes,
  unzip,
  delete_if_exists,
  instant__GT_file_time,
  xdg_state_home,
  file,
  components,
  size,
  modified_since,
  same_file_QMARK_,
  exec_paths,
  split_paths,
  windows_QMARK_,
  xdg_data_home,
  relativize,
  copy_tree,
  glob,
  set_last_modified_time,
  walk_file_tree,
  relative_QMARK_,
  create_link,
  zip,
  read_link,
  executable_QMARK_,
  sym_link_QMARK_,
  unixify,
  read_attributes,
  file_name,
  // friendly camelCase aliases
  createTempDir,
  lastModifiedTime,
  xdgConfigHome,
  fileTimeToMillis,
  posixFilePermissions,
  isRegularFile,
  whichAll,
  getAttribute,
  creationTime,
  exists,
  createFile,
  isDirectory,
  startsWith,
  fileSeparator,
  setCreationTime,
  createDirs,
  createTempFile,
  setAttribute,
  stripExt,
  setPosixFilePermissions,
  updateFile,
  splitExt,
  xdgCacheHome,
  tempDir,
  millisToFileTime,
  realPath,
  pathSeparator,
  deleteOnExit,
  writeLines,
  fileTimeToInstant,
  listDirs,
  readAllLines,
  createSymLink,
  isReadable,
  posixToStr,
  deleteTree,
  strToPosix,
  isWritable,
  createDir,
  readAttributesRaw,
  readAllBytes,
  listDir,
  isAbsolute,
  endsWith,
  isHidden,
  expandHome,
  globToRegex,
  writeBytes,
  deleteIfExists,
  instantToFileTime,
  xdgStateHome,
  modifiedSince,
  isSameFile,
  execPaths,
  splitPaths,
  isWindows,
  xdgDataHome,
  copyTree,
  setLastModifiedTime,
  walkFileTree,
  isRelative,
  createLink,
  readLink,
  isExecutable,
  isSymlink,
  readAttributes,
  fileName,
  // reserved-word names (no option translation, none needed)
  delete$ as delete,
};

// with-temp-dir is a compile-time macro; this is the JS-callable equivalent.
// Creates a temp dir, runs `callback(dir)`, then deletes it (unless `opts.keep`).
export function withTempDir(callback, opts) {
  const o = kebabizeArg(opts);
  const dir = create_temp_dir(o);
  try {
    return callback(dir);
  } finally {
    if (!(o && o.keep)) {
      delete_tree(dir, { force: true });
    }
  }
}
